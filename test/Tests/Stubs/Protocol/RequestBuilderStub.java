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
package Tests.Stubs.Protocol;

import Network.Wrappers.ISocketChannel;
import Protocol.Models.HttpContext;
import Protocol.Builders.IRequestBuilder;

/**
 *
 * @author jmillen
 */
public class RequestBuilderStub implements IRequestBuilder
{
    public Integer Status = 200;
    
    @Override
    public HttpContext ProcessRequest(ISocketChannel channel)
    {
        return new HttpContext(Status);
    }
    
}
