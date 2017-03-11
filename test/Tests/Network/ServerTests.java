/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests.Network;

import Network.Server;
import Network.Wrappers.ISelectorKeys;
import Tests.Network.Stubs.*;
import java.io.IOException;
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
        _socket = new ServerSocketStub();
        _selector = new SelectorStub();
        _unitUnderTest = new Server(_socket, _selector);
        
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
