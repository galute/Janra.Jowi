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

import Tests.Stubs.Network.*;
import Protocol.Builders.RequestBuilder;
import Protocol.Models.HttpContext;
import Protocol.Parsers.IParser;
import Protocol.Parsers.Parser;
import Server.IConfiguration;
import Tests.Stubs.Processing.ConfigStub;
import java.io.UnsupportedEncodingException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class RequestBuilderTests
{
    private RequestBuilder _unitUnderTest;
    private IParser _parser;
    private IConfiguration _config;
    
    @Before
    public void Setup()
    {
        _config = new ConfigStub();
        _parser = new Parser(1024, "UTF-8");
        _unitUnderTest = new RequestBuilder(_parser, _config);
    }
    
    @Test
    public void AddsHostToContext()
    {
        _unitUnderTest = new RequestBuilder(_parser, _config);
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nHost: 123\r\nContent-length: 0\r\n\r\n\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        assertTrue("123".equals(context.request().host()));
    }
    
    @Test
    public void HostHeaderNotInHeadersList()
    {
        _unitUnderTest = new RequestBuilder(_parser, _config);
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nHost: 123\r\nContent-length: 0\r\n\r\n\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        assertFalse(context.request() == null);
        assertEquals(null, context.request().header("host"));
    }
    
    @Test
    public void BadRequestIfMoreThanOneHostHeader()
    {
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nHost: 123\r\nHost: 456\r\n\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        
        assertEquals((long)400, (long)context.response().status());
    }
    
    @Test
    public void BadRequestIfMissingHostHeader()
    {
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nheader1: 123\r\nheader2: 456\r\n\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        
        assertEquals((long)400, (long)context.response().status());
    }
    
    @Test
    public void BadRequestIfContentLengthNaN()
    {
        _unitUnderTest = new RequestBuilder(_parser, _config);
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nHost: 123\r\nContent-length: ab\r\n\r\nhello\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        
        assertEquals((long)400, (long)context.response().status());
    }
    
    @Test
    public void ContentLengthIgnoredForTransferEncoding()
    {
        _unitUnderTest = new RequestBuilder(_parser, _config);
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nHost: 123\r\nContent-length: 12\r\nTransfer-Encoding: chunked\r\n\r\n5\r\nhello\r\n0\r\n\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        
        assertTrue(context.request().header("content-length") == null);
        assertTrue(context.request().header("transfer-encoding") != null);
    }
    
    @Test
    public void CharsetExtractedFromContentTypeHeader()
    {
        _unitUnderTest = new RequestBuilder(_parser, _config);
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nHost: 123\r\nContent-length: 12\r\nTransfer-Encoding: chunked\r\n\r\n5\r\nhello\r\n0\r\n\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        
        assertTrue(context.request().header("content-length") == null);
        assertTrue(context.request().header("transfer-encoding") != null);
    }
    
    @Test
    public void RequestBodyAddedToRequest()
    {
        try
        {
            _unitUnderTest = new RequestBuilder(_parser, _config);
            SocketStubComplete socketStub = new SocketStubComplete();
            socketStub.setMessageToRead("POST /my/request HTTP/1.1\r\nHost: 123\r\nContent-length: 5\r\nContent-type: text/plain; charset=ISO-8859-1\r\n\r\nhello\r\n");
            HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
            
            assertEquals("ISO-8859-1", context.request().charset());
        }
        catch (Exception ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ChunkedMustBeLastEncoding()
    {
        _unitUnderTest = new RequestBuilder(_parser, _config);
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nHost: 123\r\nTransfer-encoding: chunked, gzip\r\n\r\nhello\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        
        assertEquals((long)400, (long)context.response().status());
    }
    
    @Test
    public void ChunkedEncodingProcessed()
    {
        try
        {
            _unitUnderTest = new RequestBuilder(_parser, _config);
            SocketStubComplete socketStub = new SocketStubComplete();
            socketStub.setMessageToRead("POST /my/request HTTP/1.1\r\nHost: 123\r\nTransfer-encoding: chunked\r\n\r\n5\r\nhello\r\n0\r\n\r\n\r\n");
            HttpContext context = _unitUnderTest.ProcessRequest(socketStub);

            assertFalse(context.request() == null);
            assertEquals("hello", context.request().body().asString("UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void Returns501ForUnrecognisedEncodingWithChunked()
    {
        try
        {
            _unitUnderTest = new RequestBuilder(_parser, _config);
            SocketStubComplete socketStub = new SocketStubComplete();
            socketStub.setMessageToRead("POST /my/request HTTP/1.1\r\nHost: 123\r\nTransfer-encoding: blorg, chunked\r\n\r\n5\r\nhello\r\n0\r\n\r\n\r\n");
            HttpContext context = _unitUnderTest.ProcessRequest(socketStub);

            assertEquals((long)501, (long)context.response().status());
        }
        catch (Exception ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void Returns414ForMaxUriLengthExceededWithHost()
    {
        try
        {
            _config.setMaxUriLength(10);
            _unitUnderTest = new RequestBuilder(_parser, _config);
            SocketStubComplete socketStub = new SocketStubComplete();
            socketStub.setMessageToRead("POST /a/b HTTP/1.1\r\nHost: abcdefghijk\r\nTransfer-encoding: chunked\r\n\r\n5\r\nhello\r\n0\r\n\r\n\r\n");
            HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
            assertEquals((long)414, (long)context.response().status());
        }
        catch (Exception ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void Returns414ForMaxUriLengthExceededCombined()
    {
        try
        {
            _config.setMaxUriLength(10);
            _unitUnderTest = new RequestBuilder(_parser, _config);
            SocketStubComplete socketStub = new SocketStubComplete();
            socketStub.setMessageToRead("POST /a/b HTTP/1.1\r\nHost: abcdefg\r\nTransfer-encoding: chunked\r\n\r\n5\r\nhello\r\n0\r\n\r\n\r\n");
            HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
            assertEquals((long)414, (long)context.response().status());
        }
        catch (Exception ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void Returns400ForContentRangeAndPut()
    {
        try
        {
            _config.setMaxUriLength(10);
            _unitUnderTest = new RequestBuilder(_parser, _config);
            SocketStubComplete socketStub = new SocketStubComplete();
            socketStub.setMessageToRead("PUT /a/b HTTP/1.1\r\nHost: abcdefg\r\nContent-Range: foobar\r\n\r\n5\r\nhello\r\n0\r\n\r\n\r\n");
            HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
            assertEquals((long)400, (long)context.response().status());
        }
        catch (Exception ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
}
