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
package Tests.Stubs.Processing;

import Network.Wrappers.ISocketChannel;
import Protocol.Models.HttpResponse;
import Request.Processing.ISendResponse;

/**
 *
 * @author jmillen
 */
public class SendResponseStub implements ISendResponse
{
    public Integer _numCalls = 0;
    public HttpResponse Response = null;
    
    @Override
    public void sendResponse(HttpResponse response, ISocketChannel channel)
    {
        _numCalls++;
        Response = response;
    }
    
    public Integer numRequests()
    {
        return _numCalls;
    }
}
