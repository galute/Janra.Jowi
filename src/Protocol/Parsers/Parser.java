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

package Protocol.Parsers;

import Protocol.Models.HttpRequest;
import Protocol.Models.*;
import Server.IHeader;

/**
 *
 * @author jmillen
 */
public class Parser implements IParser
{
    private final Integer _maxUriLength;
    
    public Parser(Integer maxUriLength)
    {
        _maxUriLength = maxUriLength;
    }
    @Override
    public HttpRequest ParseRequestLine(String line) throws ProtocolException
    {
        String[] elements;
        
        elements = line.split(" ");
        
        if (elements.length != 3)
        {
            throw new ProtocolException("Invalid Request Line", 400);
        }
        
        if (elements[1].length() > _maxUriLength)
        {
            throw new ProtocolException("Path of Uri too long", 414);
        }
        
        if (!"HTTP/1.1".equals(elements[2]))
        {
            throw new ProtocolException("Unsupported Http version", 505);
        }
        
        HttpMethod method;
        method = HttpMethod.find(elements[0]);
        
        return new HttpRequest(method, elements[1], elements[2]);
    }
    
    @Override
    public IHeader ParseHeader(String line) throws ProtocolException
    {
        String[] elements;
        //rfc7230 section 3.2 indicates header fieldname followed by a colon (:).
        //This is followed by a value with optional leading and
        // trailing whitespace on the value
        
        elements = line.split(":\\h");
        
        int colon = line.indexOf(':');
        if (colon < 1)
        {
            throw new ProtocolException("Invalid Header format", 400);
        }
        String name = line.substring(0, colon);
        String value = line.substring(colon + 1);
        
        // section 3.2.4 states:
        // No whitespace is allowed between the header field-name and colon
        if (name.length() != name.trim().length())
        {
            throw new ProtocolException("Invalid Header format", 400);
        }
        
        return Header.create(name, value.trim());
    }
}
