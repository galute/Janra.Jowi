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

import Protocol.Parsers.ProtocolException;
import Server.IHeader;

/**
 *
 * @author jmillen
 */
public class ContentType
{
    private String _mediaType = "application/octet-stream"; // default (rfc7230 3.1.1.5)
    private String _charset;
    private final String CharSet = "charset=";
    public ContentType(String charset)
    {
        _charset = charset;
    }
    public ContentType(IHeader contentType, String charset) throws ProtocolException
    {
        _charset = charset;
        
        String[] elements = contentType.value().split(";");
        if (elements.length == 2)
        {
            String charsetField = elements[1].trim();
            
            if (!charsetField.toLowerCase().startsWith(CharSet) || 
                 charsetField.length() < CharSet.length() + 2 ||  // '=' and at least 1 character
                !charsetField.contains("="))
            {
                throw new ProtocolException("Unrecognised charset format", 415);
            }
            
            _charset = charsetField.substring(charsetField.indexOf("=") + 1);
            
            if (_charset.charAt(0) ==' ')
            {
                throw new ProtocolException("Unrecognised charset format", 415);
            }
            
            _charset = _charset.replace('"', ' ').trim();
        }
        _mediaType = elements[0].trim().toLowerCase();
    }
    
    public String mediaType()
    {
        return _mediaType;
    }
    
    public String charset()
    {
        return _charset;
    }
}
