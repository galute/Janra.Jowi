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

import Tests.Network.Stubs.SelectorExceptionStub;
import java.io.IOException;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class ServerProcessorErrorTests extends NetworkContext
{
    @Before
    public void Setup()
    {
        try
        {
            GivenConfiguredFailingServer();
            WhenAcceptingRequests();
        }
        catch (Exception ex)
        {
            fail("Setup: Exception Thrown: " + ex.getLocalizedMessage());
        }
    }
    
    
    @Test
    public void GracefullyTerminatesOnException() throws InterruptedException
    {
        try
        {
            WhenAcceptingRequests();
            Thread.sleep(100L);
            assertTrue(_processor._exceptonThrown instanceof IOException);
            
        }
        catch (IOException ex)
        {
            fail("Exception Thrown: " + ex.getMessage());
        }
    }
}
