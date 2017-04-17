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
package Tests.Protocol.Models;

import Protocol.Models.Header;
import Protocol.Models.HttpResponse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class HttpResponseTests
{
    private HttpResponse _unitUnderTest;
    
    @Before
    public void setup()
    {
        _unitUnderTest = new HttpResponse();
    }
    
    @Test
    public void ReturnsCorrectStatus()
    {
        try
        {
            _unitUnderTest.setBody("TestBody");
            _unitUnderTest.setStatus(400);
            assertTrue(_unitUnderTest.status() == 400);
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ReturnsDefaultContentHeaderWithBody()
    {
        try
        {
            _unitUnderTest.setBody("TestBody");
            String result = _unitUnderTest.getRaw();
            assertTrue(result.contains("Content-type: text/plain; charset=UTF-8"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void DoesNotReturnsDefaultContentHeaderWithoutBody()
    {
        try
        {
            String result = _unitUnderTest.getRaw();
            assertFalse(result.contains("Content-type: text/plain; charset=UTF-8"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void OnlyEverOneContentHeader()
    {
        try
        {
            _unitUnderTest.setBody("TestBody");
            _unitUnderTest.addHeader(Header.create("Content-type", "test/first"));
            _unitUnderTest.addHeader(Header.create("Content-type", "test/second"));
            String result = _unitUnderTest.getRaw();
            assertFalse(result.contains("Content-type: test/first"));
            assertTrue(result.contains("Content-type: test/second"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void DoesNotReturnsDefaultContentHeaderIfSet()
    {
        try
        {
            _unitUnderTest.addHeader(Header.create("Content-type","text/xml"));
            String result = _unitUnderTest.getRaw();
            assertFalse(result.contains("Content-type: text/plain; charset=UTF-8"));
            assertTrue(result.contains("Content-type: text/xml"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ReturnsAllSetHeaders()
    {
        try
        {
            _unitUnderTest.addHeader(Header.create("Customer-id","123456"));
            _unitUnderTest.addHeader(Header.create("Session-id","654321"));
            String result = _unitUnderTest.getRaw();
            assertTrue(result.contains("Customer-id: 123456"));
            assertTrue(result.contains("Session-id: 654321"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ReturnsBody()
    {
        try
        {
            _unitUnderTest.setBody("Test body");
            String result = _unitUnderTest.getRaw();
            assertTrue(result.contains("Test body"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ReturnsBlankLineBeforBody()
    {
        try
        {
            _unitUnderTest.setBody("Test body");
            String result = _unitUnderTest.getRaw();
            String[] lines = result.split("\r\n", -1);
            assertTrue(lines.length == 5);
            assertTrue(lines[2].isEmpty());
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ReturnsCorrectContentLength()
    {
        try
        {
            _unitUnderTest.setBody("Test body");
            String result = _unitUnderTest.getRaw();
            assertTrue(result.contains("Content-Length: 9"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ReturnsNoContentLengthIfNoBody()
    {
        try
        {
            String result = _unitUnderTest.getRaw();
            assertFalse(result.contains("Content-Length:"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
}
