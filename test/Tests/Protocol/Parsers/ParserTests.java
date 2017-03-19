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
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class ParserTests
{
    String _fullrequest;
    String _minrequest;
    IParser _parser;
    
    @Before
    public void Setup()
    {
        _parser = new Parser();
        _minrequest = "GET /\r\n\r\n";
        _fullrequest = "POST /my/resource/location HTTP/1.1\r\n" +
                   "Host: localhost:6543\r\n" +
                   "Connection: keep-alive\r\n" +
                   "Content-Length: 2023\r\n" +
                   "Postman-Token: 4f46158f-d06e-66e9-bdeb-780311945b2a\r\n" +
                   "Cache-Control: no-cache\r\n" +
                   "Origin: chrome-extension://fhbjgbiflinjbdggehcddcbncdddomop\r\n" +
                   "second-one: not me\r\n" +
                   "firstone: me\r\n" +
                   "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36\r\n" +
                   "Content-Type: text/plain;charset=UTF-8\r\n" +
                   "Accept: */*\r\n" +
                   "Accept-Encoding: gzip, deflate, br\r\n" +
                   "Accept-Language: en-GB,en-US;q=0.8,en;q=0.6\r\n" +
                   "\r\n" +
                   "{\r\n" +
                   "   field1: value1,\r\n" +
                   "   field2: value2\r\n" +
                   "}\r\n" +
                   "\r\n";
    }
    
    @Test
    public void CorrectlyExtractsFirstLine()
    {
        try
        {
            HttpRequest result = _parser.Parse(_fullrequest);
            
            assertTrue("POST".equals(HttpMethod.POST.toString()));
            assertTrue("/my/resource/location".equals(result.Path));
            assertTrue("HTTP/1.1".equals(result.Version));
        }
        catch (Exception ex)
        {
            fail("Exception thrown: " + ex);
        }   
    }
    
    @Test
    public void CorrectlyExtractsMinimumFirstLine()
    {
        try
        {
            HttpRequest result = _parser.Parse(_minrequest);
            
            assertTrue("GET".equals(HttpMethod.GET.toString()));
            assertTrue("/".equals(result.Path));
            assertTrue(result.Version == null);
        }
        catch (Exception ex)
        {
            fail("Exception thrown: " + ex);
        }   
    }

    @Test
    public void CorrectlyHandlesBadFirstLineFormat()
    {
        try
        {
            String badRequest = "GET /\r\n";
            HttpRequest result = _parser.Parse(badRequest);
            
            fail("No Exception thrown");
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof ProtocolException);
        }
    }
    
    @Test
    public void CorrectlyExtractsHost()
    {
        try
        {
            HttpRequest result = _parser.Parse(_fullrequest);
            
            assertTrue("localhost:6543".equals(result.Host));
        }
        catch (Exception ex)
        {
            fail("Exception thrown: " + ex);
        }
    }
    
    @Test
    public void CorrectlyHandlesNoHost()
    {
        try
        {
            HttpRequest result = _parser.Parse(_minrequest);
            
            assertTrue(result.Host == null);
        }
        catch (Exception ex)
        {
            fail("Exception thrown: " + ex);
        }
    }
    
    @Test
    public void CorrectlyHandlesBadHost() // missing colon
    {
        try
        {
            String badHost = "POST /my/resource/location HTTP/1.1\r\n" +
                             "Host localhost:6543\r\n";
            HttpRequest result = _parser.Parse(badHost);
            
            assertTrue(result.Host == null);
        }
        catch (Exception ex)
        {
            fail("Exception thrown: " + ex);
        }
    }
}
