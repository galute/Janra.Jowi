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

import Protocol.Models.HttpRequest;
import Protocol.Parsers.*;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class ParserTests
{
    String _request;
    IParser _parser;
    
    @Before
    public void Setup()
    {
        _parser = new Parser();
        _request = "GET /my/resource/location HTTP/1.1\r\n" +
                   "Host: localhost:6543\r\n" +
                   "Connection: keep-alive\r\n" +
                   "Upgrade-Insecure-Requests: 1\r\n" +
                   "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36\r\n" +
                   "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n" +
                   "Accept-Encoding: gzip, deflate, sdch, br\r\n" +
                   "Accept-Language: en-GB,en-US;q=0.8,en;q=0.6\r\n" +
                   "Cookie: Rider-12145af8=199fbc8c-1b75-4bb4-8575-f91111b19480";
    }
    
    @Test
    public void CorrectlyExtractsFirstLine()
    {
        try
        {
            HttpRequest result = _parser.Parse(_request);
            
            assertTrue("GET".equals(result.method));
            assertTrue("/my/resource/location".equals(result.path));
            assertTrue("HTTP/1.1".equals(result.version));
        }
        catch (Exception ex)
        {
            fail("Exception thrown: {0}", ex);
        }
        
    }

    private void fail(String exception_thrown_0, Exception ex)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
