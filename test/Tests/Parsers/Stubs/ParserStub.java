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
package Tests.Parsers.Stubs;

import Protocol.Models.HttpMethod;
import Protocol.Models.HttpRequest;
import Protocol.Parsers.IParser;
import java.util.HashMap;

/**
 *
 * @author jmillen
 */
public class ParserStub implements IParser
{
    public String PassedBuffer;
    @Override
    public HttpRequest Parse(String buffer)
    {
        PassedBuffer = buffer;
        
        return new HttpRequest(HttpMethod.GET, "/", "HTTP/1.1", "my.host", new HashMap<>());
    }
    
}
