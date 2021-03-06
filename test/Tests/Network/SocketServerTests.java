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

import Tests.Stubs.Network.SelectorKeysStub;
import Tests.Stubs.Network.SelectorStub;
import java.io.IOException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author jmillen
 */
public class SocketServerTests extends NetworkContext
{
    @Before
    public void Setup()
    {
        GivenConfiguredServer();
    }
    
    @After
    public void Cleanup() throws IOException
    {
        _server.Close();
    }
    
    @Test
    public void BoundToCorrectPortAndConfigured()
    {
        assertFalse(_socket._nonBlockingFlag);
        assertFalse(_socket._isClosed);
        assertEquals(_port,_socket._boundPort);
        assertEquals(Integer.valueOf(1),((SelectorStub)_selector)._registeredForAccept);
    }
    
    @Test
    public void HandlesAllPendingRequests()
    {
        Integer numRequests = 5;
        SelectorKeysStub keys = null;
        try
        {
            GivenIncomingRequests(numRequests);
            keys = (SelectorKeysStub)WhenCheckingForPendingRequests(100L);
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
            
            WhenCheckingForPendingRequests(100L);
            
            fail("Exception not Thrown when server is closed");
        }
        catch (Exception ex)
        {
            assertEquals(ex.getMessage(), "Socket is closed");
        }
    }
}
