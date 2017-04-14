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

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author jmillen
 */
public class HttpResponse
{
    private Integer _status;
    private final Headers _headers;
    private String _body = "";
    
    public HttpResponse()
    {
        this._headers = new Headers();
        _status = 200;
    }
    
    public HttpResponse(Integer status)
    {
        this._headers = new Headers();
        _status = status;
    }
    
    public void setStatus(Integer status)
    {
        _status = status;
    }
    
    public void setBody(String body)
    {
        _body = body;
    }
    
    public Integer status()
    {
        return _status;
    }
    
    public void addHeader(Header header)
    {
        _headers.addHeader(header);
    }
    
    public String getRaw()
    {
        String retVal = "";
        Boolean hasContentType = false;
        
        Iterator iter = _headers.getIterator();
        
        while (iter.hasNext())
        {
            Map.Entry pair = (Map.Entry)iter.next();
        
            retVal = retVal + MessageFormat.format("{0}: {1}\r\n", pair.getKey(), pair.getValue());
            if ("Content-type".equals(pair.getKey()))
            {
                hasContentType = true;
            }
            iter.remove();
        }
        
        if (!_body.isEmpty())
        {
            if (!hasContentType)
            {
                retVal = retVal + "Content-type: text/plain; charset=UTF-8\r\n";
            }
            
            retVal = MessageFormat.format("{0}Content-Length: {1}\r\n\r\n", retVal, _body.length());
            retVal = retVal + _body + "\r\n";
        }

        return retVal;
    }
}
