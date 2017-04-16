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
package Tests.Protocol.Processing;

import Protocol.Models.HttpContext;
import Request.Processing.RequestContext;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class RequestContextTests
{
    private RequestContext _unitUnderTest;
    private HttpContext _httpContext;
    
    @Before
    public void setup()
    {
        _httpContext = new HttpContext(200);
        _unitUnderTest = new RequestContext(_httpContext);
    }
    
    @Test
    public void PropertiesAreUpDated()
    {
        _unitUnderTest.Properties().add("TestKey", "TestValue");
        assertTrue(_unitUnderTest.Properties().Property("TestKey") == "TestValue");
    }
    
    @Test
    public void PropertiesAreNotInResponse()
    {
        try
        {
            _unitUnderTest.Properties().add("TestKey", "TestValue");
            assertFalse(_httpContext.response().getRaw().contains("TestKey"));
            assertFalse(_httpContext.response().getRaw().contains("TestValue"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ResponseStatusIsUpDated()
    {
        _unitUnderTest.setResponseStatus(400);
        assertTrue(_httpContext.response().status() == 400);
    }
    
    @Test
    public void ResponseBodyIsUpDated()
    {
        try
        {
            _unitUnderTest.setResponseBody("Test Body");
            assertTrue(_httpContext.response().getRaw().contains("Test Body"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ResponseHeaderIsAdded()
    {
        try
        {
            _unitUnderTest.addResponseHeader("TestHeaderKey", "TestHeaderValue");
            assertTrue(_httpContext.response().getRaw().contains("TestHeaderKey: TestHeaderValue"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
}
