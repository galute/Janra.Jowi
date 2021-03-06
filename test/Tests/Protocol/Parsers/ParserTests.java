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
package Tests.Protocol.Parsers;

import Protocol.Models.HttpMethod;
import Protocol.Models.HttpRequest;
import Protocol.Parsers.*;
import Server.IHeader;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class ParserTests
{
    private String _badRequest;
    private String _goodRequest;
    private String _unsupportedRequest;
    private String _goodHeader1;
    private String _goodHeader2;
    private String _goodHeader3;
    private String _goodHeader4;
    private String _badHeader1;
    private String _badHeader2;
    private String _badHeader3;
    
    private IParser _parser;
    
    @Before
    public void Setup()
    {
        _parser = new Parser(1024, "UTF-8");
        _goodRequest = "POST /my/resource/location HTTP/1.1";
        _badRequest = "POST HTTP/1.1";
        _unsupportedRequest = "POST /my/resource/location HTTP/1.0";
        //rfc7230 section 3.2 indicates header fieldname followed by a colon (:).
        // This is followed by a value with optional leading and
        // trailing whitespace on the value.
        _goodHeader1 = "Host: my.host:80";
        _goodHeader2 = "Host:  my.host:80  ";
        _goodHeader3 = "Host:   my.host:80";
        _goodHeader4 = "Host:my.host:80";
        _badHeader1 = "Host my.host80";
        _badHeader2 = "Hostmy.host80";
        _badHeader3 = "Host :  my.host:80";

        
    }
    
    @Test
    public void CorrectlyExtractsRequestLine()
    {
        try
        {
            HttpRequest result = _parser.ParseRequestLine(_goodRequest);
            
            assertEquals("POST", HttpMethod.POST.toString());
            assertEquals("/my/resource/location", result.path());
            assertEquals("HTTP/1.1", result.version());
        }
        catch (Exception ex)
        {
            fail("Exception thrown: " + ex);
        }
    }
    
    @Test
    public void CorrectlyRejectsBadRequestLine()
    {
        try
        {
            _parser.ParseRequestLine(_badRequest);
            fail("Exception not thrown");
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertEquals("Invalid Request Line", ex.getMessage());
            
            ProtocolException pEx = (ProtocolException)ex;
            
            assertTrue(pEx.ResponseStatus == 400);
        }  
    }
    
    @Test
    public void CorrectlyRejectsUnsupportedVersion()
    {
        try
        {
            _parser.ParseRequestLine(_unsupportedRequest);
            fail("Exception not thrown");
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertEquals("Unsupported Http version", ex.getMessage());
            ProtocolException pEx = (ProtocolException)ex;
            
            assertTrue(pEx.ResponseStatus == 505);
        }  
    }
    @Test
    public void RejectsUriTooLong()
    {    
        try
        {
            _parser = new Parser(10, "UTF-8");
            _parser.ParseRequestLine(_goodRequest);
            fail("Exception not thrown");
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertEquals("Path of Uri too long", ex.getMessage());
            
            ProtocolException pEx = (ProtocolException)ex;
            
            assertTrue(pEx.ResponseStatus == 414);
        }
    }
    
    @Test
    public void CorrectlyExtractsHeader1()
    {
        try
        {
            IHeader result = _parser.ParseHeader(_goodHeader1);
            
            assertEquals("Host", result.key());
            assertEquals("my.host:80", result.value(0));
        }
        catch (Exception ex)
        {
            fail("Exception thrown: " + ex);
        }
    }
    
    @Test
    public void CorrectlyExtractsHeader2()
    {
        try
        {
            IHeader result = _parser.ParseHeader(_goodHeader2);
            
            assertEquals("Host", result.key());
            assertEquals("my.host:80", result.value(0));
        }
        catch (Exception ex)
        {
            fail("Exception thrown: " + ex);
        }
    }
    
    @Test
    public void CorrectlyExtractsHeader3()
    {
        try
        {
            IHeader result = _parser.ParseHeader(_goodHeader3);
            
            assertEquals("Host", result.key());
            assertEquals("my.host:80", result.value(0));
        }
        catch (Exception ex)
        {
            fail("Exception thrown: " + ex);
        }
    }
    
    @Test
    public void CorrectlyExtractsHeader4()
    {
        try
        {
            IHeader result = _parser.ParseHeader(_goodHeader4);
            
            assertEquals("Host", result.key());
            assertEquals("my.host:80", result.value(0));
        }
        catch (Exception ex)
        {
            fail("Exception thrown: " + ex);
        }
    }
    
    @Test
    public void CorrectlyRejectsBadHeader1()
    {
        try
        {
            _parser.ParseHeader(_badHeader1);
            fail("Exception not thrown");
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertEquals("Invalid Header format", ex.getMessage());
        }  
    }
    
    @Test
    public void CorrectlyRejectsBadHeader2()
    {
        try
        {
            _parser.ParseHeader(_badHeader2);
            fail("Exception not thrown");
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertEquals("Invalid Header format", ex.getMessage());
        }  
    }
    
    @Test
    public void CorrectlyRejectsBadHeader3()
    {
        try
        {
            _parser.ParseHeader(_badHeader3);
            fail("Exception not thrown");
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertEquals("Invalid Header format", ex.getMessage());
        }  
    }
    
    @Test
    public void PassesCharsetToRequest()
    {
        try
        {
            _parser = new Parser(1024, "FooBar");
            HttpRequest result = _parser.ParseRequestLine(_goodRequest);
            
            assertEquals("FooBar", result.charset());
        }
        catch (Exception ex)
        {
            fail("Exception thrown: " + ex);
        }
    }
}