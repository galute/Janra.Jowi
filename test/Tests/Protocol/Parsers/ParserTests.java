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

import Protocol.Models.Header;
import Protocol.Models.HttpMethod;
import Protocol.Models.HttpRequest;
import Protocol.Parsers.*;
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
    private String _badHeader1;
    private String _badHeader2;
    private String _badHeader3;
    
    private IParser _parser;
    
    @Before
    public void Setup()
    {
        _parser = new Parser();
        _goodRequest = "POST /my/resource/location HTTP/1.1";
        _badRequest = "POST HTTP/1.1";
        _unsupportedRequest = "POST /my/resource/location HTTP/1.0";
        _goodHeader1 = "Host: my.host:80";
        _goodHeader2 = "Host :  my.host:80";
        _goodHeader3 = "Host:   my.host:80";
        _badHeader1 = "Host:my.host:80";
        _badHeader2 = "Host my.host:80";
        _badHeader3 = "Hostmy.host:80";

        
    }
    
    @Test
    public void CorrectlyExtractsRequestLine()
    {
        try
        {
            HttpRequest result = _parser.ParseRequestLine(_goodRequest);
            
            assertTrue("POST".equals(HttpMethod.POST.toString()));
            assertTrue("/my/resource/location".equals(result.path()));
            assertTrue("HTTP/1.1".equals(result.version()));
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
            assertTrue("Invalid Request Line".equals(ex.getMessage()));
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
            assertTrue("Unsupported Http version".equals(ex.getMessage()));
        }  
    }
    
    @Test
    public void CorrectlyExtractsHeader1()
    {
        try
        {
            Header result = _parser.ParseHeader(_goodHeader1);
            
            assertTrue("Host".equals(result.key()));
            assertTrue("my.host:80".equals(result.value()));
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
            Header result = _parser.ParseHeader(_goodHeader2);
            
            assertTrue("Host".equals(result.key()));
            assertTrue("my.host:80".equals(result.value()));
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
            Header result = _parser.ParseHeader(_goodHeader3);
            
            assertTrue("Host".equals(result.key()));
            assertTrue("my.host:80".equals(result.value()));
        }
        catch (Exception ex)
        {
            fail("Exception thrown: " + ex);
        }
    }
    
    @Test
    public void CorrectlyRejectsBadHeader()
    {
        try
        {
            _parser.ParseHeader(_badHeader1);
            fail("Exception not thrown");
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertTrue("Invalid Header format".equals(ex.getMessage()));
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
            assertTrue("Invalid Header format".equals(ex.getMessage()));
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
            assertTrue("Invalid Header format".equals(ex.getMessage()));
        }  
    }
}
//_fullrequest = "POST /my/resource/location HTTP/1.1\r\n" +
//                   "Host: localhost:6543\r\n" +
//                   "Connection: keep-alive\r\n" +
//                   "Content-Length: 2023\r\n" +
//                   "Postman-Token: 4f46158f-d06e-66e9-bdeb-780311945b2a\r\n" +
//                   "Cache-Control: no-cache\r\n" +
//                   "Origin: chrome-extension://fhbjgbiflinjbdggehcddcbncdddomop\r\n" +
//                   "second-one: not me\r\n" +
//                   "firstone: me\r\n" +
//                   "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36\r\n" +
//                   "Content-Type: text/plain;charset=UTF-8\r\n" +
//                   "Accept: */*\r\n" +
//                   "Accept-Encoding: gzip, deflate, br\r\n" +
//                   "Accept-Language: en-GB,en-US;q=0.8,en;q=0.6\r\n" +
//                   "\r\n" +
//                   "{\r\n" +
//                   "   field1: value1,\r\n" +
//                   "   field2: value2\r\n" +
//                   "}\r\n" +
//                   "\r\n";