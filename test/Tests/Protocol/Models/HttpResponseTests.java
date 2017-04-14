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
    public void ReturnsDefaultContentHeaderWithBody()
    {
        _unitUnderTest.setBody("TestBody");
        String result = _unitUnderTest.getRaw();
        assertTrue(result.contains("Content-type: text/plain; charset=UTF-8"));
    }
    
    @Test
    public void DoesNotReturnsDefaultContentHeaderWithoutBody()
    {
        String result = _unitUnderTest.getRaw();
        assertFalse(result.contains("Content-type: text/plain; charset=UTF-8"));
    }
    
    @Test
    public void OnlyEverOneContentHeader()
    {
        _unitUnderTest.setBody("TestBody");
        _unitUnderTest.addHeader(new Header("Content-type", "test/first"));
        _unitUnderTest.addHeader(new Header("Content-type", "test/second"));
        String result = _unitUnderTest.getRaw();
        assertFalse(result.contains("Content-type: test/first"));
        assertTrue(result.contains("Content-type: test/second"));
    }
    
    @Test
    public void DoesNotReturnsDefaultContentHeaderIfSet()
    {
        _unitUnderTest.addHeader(new Header("Content-type","text/xml"));
        String result = _unitUnderTest.getRaw();
        assertFalse(result.contains("Content-type: text/plain; charset=UTF-8"));
        assertTrue(result.contains("Content-type: text/xml"));
    }
    
    @Test
    public void ReturnsAllSetHeaders()
    {
        _unitUnderTest.addHeader(new Header("Customer-id","123456"));
        _unitUnderTest.addHeader(new Header("Session-id","654321"));
        String result = _unitUnderTest.getRaw();
        assertTrue(result.contains("Customer-id: 123456"));
        assertTrue(result.contains("Session-id: 654321"));
    }
    
    @Test
    public void ReturnsBody()
    {
        _unitUnderTest.setBody("Test body");
        String result = _unitUnderTest.getRaw();
        assertTrue(result.contains("Test body"));
    }
    
    @Test
    public void ReturnsBlankLineBeforBody()
    {
        _unitUnderTest.setBody("Test body");
        String result = _unitUnderTest.getRaw();
        String[] lines = result.split("\r\n", -1);
        assertTrue(lines.length == 5);
        assertTrue(lines[2].isEmpty());
    }
    
    @Test
    public void ReturnsCorrectContentLength()
    {
        _unitUnderTest.setBody("Test body");
        String result = _unitUnderTest.getRaw();
        assertTrue(result.contains("Content-Length: 9"));
    }
    
    @Test
    public void ReturnsNoContentLengthIfNoBody()
    {
        String result = _unitUnderTest.getRaw();
        assertFalse(result.contains("Content-Length:"));
    }
}
