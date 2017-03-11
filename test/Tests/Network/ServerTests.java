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
import Network.Wrappers.ISelectorKeys;
import Tests.Network.Stubs.*;
import Tests.Factories.*;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author jmillen
 */
public class ServerTests
{
    Server _unitUnderTest;
    SelectorStub _selector;
    ServerSocketStub _socket;
    Integer _port = 1234;
    
    protected void GivenIncomingRequests(Integer num)
    {
        _selector._numKeysToSelect = num;
    }
    
    protected ISelectorKeys WhenCheckingForPendingRequests() throws IOException
    {
        return _unitUnderTest.Start();
    }
    
    protected void GivenServerIsClosed() throws IOException
    {
        _unitUnderTest.Close();
    }
    
    @Before
    public void Setup()
    {
        Map<String, Object> created = ServerStubFactory.Create();
        _socket = (ServerSocketStub) created.get("ServerSocketStub");
        _selector = (SelectorStub) created.get("SelectorStub");
        _unitUnderTest = (Server) created.get("Server");
        
        try
        {
            _unitUnderTest.Configure(_port);
        }
        catch (Exception ex)
        {
            fail("Exception Thrown: " + ex.getLocalizedMessage());
        }
    }
    
    @Test
    public void BoundToCorrectPortAndConfigured()
    {
        assertTrue(_socket._nonBlockingFlag);
        assertFalse(_socket._isClosed);
        assertEquals(_port,_socket._boundPort);
        assertEquals(Integer.valueOf(1),_selector._registeredForAccept);
    }
    
    @Test
    public void HandlesAllPendingRequests()
    {
        Integer numRequests = 5;
        SelectorKeysStub keys = null;
        try
        {
            GivenIncomingRequests(numRequests);
            keys = (SelectorKeysStub)WhenCheckingForPendingRequests();
        }
        catch (Exception ex)
        {
            fail("Exception Thrown: " + ex.getLocalizedMessage());
        }
        
        assertNotNull(keys);
        assertEquals(keys.NumKeys(), numRequests);
    }
    
    @Test
    public void ThrowsExceptionIfCalledWhenClosed()
    {
        try
        {
            GivenServerIsClosed();
            
            WhenCheckingForPendingRequests();
            
            fail("Exception not Thrown when server is closed");
        }
        catch (Exception ex)
        {
            assertEquals(ex.getMessage(), "Socket is closed");
        }
    }
}
