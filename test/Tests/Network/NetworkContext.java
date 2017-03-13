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
package Tests.Network;

import Network.Server;
import Network.Handlers.IncomingRequestHandler;
import Network.Wrappers.ISelector;
import Network.Wrappers.ISelectorKeys;
import Tests.Factories.*;
import Tests.Network.Stubs.*;
import java.io.IOException;
import java.util.Map;
import static org.junit.Assert.fail;

/**
 *
 * @author jmillen
 */
public class NetworkContext
{
    
    protected ISelector _selector;
    protected ServerSocketStub _socket;
    protected Server _server;
    protected Integer _port = 1234;
    protected IncomingRequestHandler _processor;
    
    protected void GivenProcessorToldToStop()
    {
        _processor.Stop();
    }
    
    protected void WhenAcceptingRequests() throws IOException
    {
        _processor = new IncomingRequestHandler(_server, _port, 10L);
        Thread thread = new Thread(_processor);
        thread.start();
    }
    
    protected ISelectorKeys WhenCheckingForPendingRequests(long timeout) throws IOException
    {
        return _server.Start(timeout);
    }
    
    protected void GivenServerIsClosed() throws IOException
    {
        _server.Close();
    }
    
    protected void GivenIncomingRequests(Integer num)
    {
        ((SelectorStub)_selector)._numKeysToSelect = num;
    }
    
    protected void GivenAcceptableKeys()
    {
        ((SelectorStub)_selector)._setAcceptable = true;
    }
    
    protected void GivenConfiguredServer()
    {
        Map<String, Object> created = ServerStubFactory.Create();
        _socket = (ServerSocketStub) created.get("ServerSocketStub");
        _selector = (SelectorStub) created.get("SelectorStub");
        _server = (Server) created.get("Server");
        
        try
        {
            _server.Configure(_port);
        }
        catch (Exception ex)
        {
            fail("Exception Thrown: " + ex.getLocalizedMessage());
        }
    }
    
    protected void GivenConfiguredFailingServer()
    {
        Map<String, Object> created = ServerStubSelectorExceptionFactory.Create();
        _socket = (ServerSocketStub) created.get("ServerSocketStub");
        _selector = (SelectorExceptionStub) created.get("SelectorStub");
        _server = (Server) created.get("Server");
        
        try
        {
            _server.Configure(_port);
        }
        catch (Exception ex)
        {
            fail("Exception Thrown: " + ex.getLocalizedMessage());
        }
    }
}
