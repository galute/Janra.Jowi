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

import Server.IHeader;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author jmillen
 */
public class Headers
{
    private final HashMap<String, IHeader> _headers;
    
    public Headers()
    {
        _headers = new HashMap<>();
    }
    public Headers(Headers headers)
    {
        _headers = (HashMap)headers._headers.clone();
    }
    
    public void addHeader(IHeader header)
    {
        String lowerKey = header.key().toLowerCase();
        if (_headers.containsKey(lowerKey))
        {
            if (!specialCase(header))
            {
                Header multi = (Header)_headers.get(lowerKey);
                multi.addHeader(header);
            }
        }
        else
        {
            _headers.put(lowerKey, header);
        }
    }
    
    public IHeader get(String key)
    {
        if (key == null)
        {
            return null;
        }
        // rfc7320 section 3.2 header field name
        // is case-insensitive so always to-lower name
        String lowerKey = key.toLowerCase();
        if (_headers.containsKey(lowerKey))
        {
            return _headers.get(lowerKey);
        }
        
        return null;
    }
    
    public Iterator getIterator()
    {
        return _headers.entrySet().iterator();
    }
    
    private Boolean specialCase(IHeader header)
    {
        String lowerKey = header.key().toLowerCase();
        if ("content-type".equals(lowerKey))
        {
            // Only allowed one of these
            _headers.replace(lowerKey, header);
            return true;
        }
        
        return false;
    }
}
