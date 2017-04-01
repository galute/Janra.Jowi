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
package Tests.Protocol.Builders;

import Protocol.Builders.ResponseBuilder;
import Protocol.Models.HttpResponse;
import Protocol.Parsers.ProtocolException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author jmillen
 */
public class ResponseBuilderTests
{
    private ResponseBuilder _unitUnderTest;
    private HttpResponse _response;
    
    @Before
    public void Setup()
    {
        _unitUnderTest = new ResponseBuilder();
        _response = new HttpResponse();
    }
    
    @Test
    public void ReturnsCorrectStatusDetails()
    { 
        _response.setStatus(501);
        
        try
        {
            ByteBuffer result = _unitUnderTest.BuildResponse(_response);
            String resultStr = new String(result.array());
            
            assertTrue(resultStr.contains("501 Not Implemented"));
        }
        catch (ProtocolException | CharacterCodingException ex)
        {
            fail("Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ReturnsServerHeaderDetails()
    {
        try
        {
            ByteBuffer result = _unitUnderTest.BuildResponse(_response);
            String resultStr = new String(result.array());
            
            assertTrue(resultStr.contains("Server: Jowi"));
        }
        catch (ProtocolException | CharacterCodingException ex)
        {
            fail("Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ReturnsServerResponseBody()
    {
        try
        {
            _response.setBody("Body Test");
            ByteBuffer result = _unitUnderTest.BuildResponse(_response);
            String resultStr = new String(result.array());
            
            assertTrue(resultStr.contains("Body Test"));
        }
        catch (ProtocolException | CharacterCodingException ex)
        {
            fail("Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void AllLineEndingsAreSlashrSlashn()
    {
        try
        {
            _response.setBody("Body Test");
            ByteBuffer result = _unitUnderTest.BuildResponse(_response);
            String resultStr = new String(result.array());
            String[] lines = resultStr.split("\r\n", -1);
            
            assertTrue(lines.length == 7);
            assertTrue(lines[4].isEmpty()); // empty line before body
            assertTrue(lines[6].isEmpty()); // last line must be empty
        }
        catch (ProtocolException | CharacterCodingException ex)
        {
            fail("Exception thrown: " + ex.getMessage());
        }
    }
}
