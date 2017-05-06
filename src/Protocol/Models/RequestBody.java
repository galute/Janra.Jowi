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

import java.io.UnsupportedEncodingException;

/**
 *
 * @author jmillen
 */
public class RequestBody
{
    private final byte[] _body;
    
    public RequestBody(byte[] body)
    {
        _body = body;
    }
    
    public String asString(String encoding) throws UnsupportedEncodingException
    {
        byte[] returnVal = new byte[_body.length];
        System.arraycopy(_body, 0, returnVal, 0, _body.length);
        return new String(returnVal, encoding);
    }
    
    public byte[] raw()
    {
        return _body;
    }
}
