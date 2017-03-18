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

import Tests.Network.Stubs.SelectorKeyStub;
import Tests.Network.Stubs.SelectorStub;
import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author jmillen
 */
public class IncomingRequestHandlerTests extends NetworkContext
{
    @Before
    public void Setup()
    {
        try
        {
            GivenConfiguredServer();
        }
        catch (Exception ex)
        {
            fail("Setup: Exception Thrown: " + ex.getLocalizedMessage());
        }
    }
    
    @After
    public void TearDown()
    {
        _processor.Stop();
    }
    
    @Test
    public void StopsWhenStopFlagSet()
    {
        try
        {
            WhenAcceptingRequests();
            GivenProcessorToldToStop();
        }
        catch (Exception ex)
        {
            fail("Test: Exception Thrown: " + ex.getLocalizedMessage());
        }  
    }
    
    @Test
    public void StartdoesNotBlock()
    {
        try
        {
            Integer numRequests = 1;
            
            GivenAcceptableKeys();
            GivenIncomingRequests(numRequests);
            WhenAcceptingRequests();
            Thread.sleep(500);
            _processor.Stop();
            
            assertEquals(numRequests, ((SelectorStub)_selector)._registeredForRead);
        }
        catch (IOException | InterruptedException ex)
        {
            fail("Exception Thrown: " + ex.getLocalizedMessage());
        }
    }
    
    @Test
    public void NotAcceptableKeyIsCancelled()
    {
        try
        {
            Integer numRequests = 1;
            
            WhenAcceptingRequests();
            GivenIncomingRequests(numRequests);
            SelectorStub sel = (SelectorStub)_selector;
            Thread.sleep(100);

            assertFalse(sel._returnedKeys == null);
            assertFalse(sel._returnedKeys._keysKept.isEmpty());
            
            assertTrue(((SelectorKeyStub)sel._returnedKeys._keysKept.get(0))._isCancelled);
        }
        catch (IOException | InterruptedException ex)
        {
            fail("Exception Thrown: " + ex.getLocalizedMessage());
        }
        
    }
}
