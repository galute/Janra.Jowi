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
package Tests.Utilities;

import Tests.Stubs.Utilities.LaunchableStub;
import Utilities.ThreadLauncher;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class LauncherTests
{
    private ThreadLauncher _unitUnderTest;
    private LaunchableStub _launchable;
    private LaunchableStub _launchable2;
    
    @Before
    public void setup()
    {
        _launchable = new LaunchableStub();
        _launchable2 = new LaunchableStub();
    }
    
    @After
    public void tearDown()
    {
        _launchable.WaitForMe = false;
        _launchable2.WaitForMe = false;
    }
    
    public void GivenNewLauncher(Integer numThreads)
    {
        _unitUnderTest = new ThreadLauncher(numThreads);
    }
    
    @Test
    public void ReturnsFailureIfMaxThreadsReached()
    {
        GivenNewLauncher(1);
        
        long result = _unitUnderTest.launch(_launchable);
        
        assertFalse(result == -1);
        
        result = _unitUnderTest.launch(_launchable2);
        
        assertTrue(result == -1);
    }
    
    @Test
    public void AllowsConfiguredNumThreadsToRun()
    {
        GivenNewLauncher(2);
        
        long result = _unitUnderTest.launch(_launchable);
        
        assertFalse(result == -1);
        
        result = _unitUnderTest.launch(_launchable2);
        
        assertFalse(result == -1);
    }
}
