/*
 * Copyright (C) 2017 jmillen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Protocol.Models;

import Server.HttpResponse;
import Protocol.Parsers.ProtocolException;
import Server.IHeader;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author jmillen
 */
public class ResponseImpl implements HttpResponse
{
    private Integer _status;
    private final Headers _headers;
    private String _body = "";
    private Boolean _bodyIsValid = true;
    private Boolean _headRequest = false;
    
    public ResponseImpl()
    {
        this._headers = new Headers();
        _status = 200;
    }
    
    public ResponseImpl(Integer status)
    {
        this._headers = new Headers();
        _status = status;
    }
    
    public void setStatus(Integer status)
    {
        _status = status;
    }
    
    public void bodyIsInvalid()
    {
        _bodyIsValid = false;
    }
    
    public void isHeadRequest()
    {
        _headRequest = true;
    }
    
    public void setBody(String body)
    {
        _body = body;
    }
    
    public Integer status()
    {
        return _status;
    }
    
    public void addHeader(IHeader header)
    {
        _headers.addHeader(header);
        // To-do, will need to change this, can have multiple of some
        // headers, e.g. Link
    }
    
    public IHeader header(String name)
    {
        return _headers.get(name);
    }
    
    public String getRaw() throws ProtocolException
    {
        Boolean needsContentType = true;
        Boolean needsContentLength = true;
        // Not supporting keep-alive at the moment
        String retVal = "Connection: close\r\n";
        
        Iterator iter = _headers.getIterator();
        
        while (iter.hasNext())
        {
            Map.Entry pair = (Map.Entry)iter.next();
            
            if (!"connection".equals(((String)pair.getKey()).toLowerCase()))
            {
                if (!_bodyIsValid && 
                    ("content-length".equals(((String)pair.getKey()).toLowerCase()) || 
                     "content-type".equals(((String)pair.getKey()).toLowerCase()) ||
                     "transfer-encoding".equals(((String)pair.getKey()).toLowerCase())))
                {
                    continue;
                }
                //rfc7230 section 3.2 indicates header fieldname followed by a colon (:). This is followed by a value with optional leading and
                // trailing whitespace on the value
                retVal = retVal + MessageFormat.format("{0}: {1}\r\n", ((IHeader)pair.getValue()).key(), ((IHeader)pair.getValue()).value(0));
            }
            
            if ("content-type".equals(((String)pair.getKey()).toLowerCase()))
            {
                needsContentType = false;
            }
            
            if ("transfer-encoding".equals(((String)pair.getKey()).toLowerCase()))
            {
                needsContentLength = false;
            }
            
            iter.remove();
        }
        
        if (!_body.isEmpty() && _bodyIsValid)
        {
            if (needsContentType)
            {
                retVal = retVal + "Content-type: text/plain; charset=UTF-8\r\n";
            }
            
            if (needsContentLength)
            {
                retVal = MessageFormat.format("{0}Content-Length: {1}\r\n\r\n", retVal, _body.length());
            }
            
            if (!_headRequest)
            {
                retVal = retVal + _body + "\r\n";
            }
        }

        return retVal;
    }
}
