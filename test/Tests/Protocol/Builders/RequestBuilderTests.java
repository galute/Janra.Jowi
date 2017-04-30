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
import Protocol.Parsers.ProtocolException;
import Protocol.Builders.RequestBuilder;
import Protocol.Models.HttpContext;
import Protocol.Parsers.IParser;
import Protocol.Parsers.Parser;
import Tests.Stubs.Protocol.ParserStub;
import java.io.IOException;
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
    private ParserStub _parser;
    
    @Before
    public void Setup()
    {
        _parser = new ParserStub();
        _unitUnderTest = new RequestBuilder(_parser);
    }
    
    @Test
    public void TimesOutIfNoDataAfterMaxRetries()
    {
        SocketStubComplete socketStub = new SocketStubComplete();
        try
        {
            socketStub.setBytestoRead(0);
            _unitUnderTest.readLine(socketStub);
        }
        catch (IOException | ProtocolException ex)
        {
            assertTrue(socketStub.NumReads == 5);
            assertTrue(ex instanceof IOException);
            assertTrue("Timeout after max retries of 5".equals(ex.getMessage()));
        }
    }
    
    @Test
    public void RequestProcessorThrowsIfWrongLineEnding()
    {
        try
        {
            SocketStubIncomplete socketStub = new SocketStubIncomplete();
            socketStub.BytesToRead = 10;
            _unitUnderTest.readLine(socketStub);
        }
        catch (IOException | ProtocolException ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertTrue("Unable to process incomplete data".equals(ex.getMessage()));
        }
    }
    
    @Test
    public void RequestProcessorThrowsIfPartialLineEnding()
    {
        try
        {
            SocketStubBadNewLine socketStub = new SocketStubBadNewLine();
            socketStub.setBytestoRead(10);
            _unitUnderTest.readLine(socketStub);
        }
        catch (IOException | ProtocolException ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertTrue("Unexpected new line character".equals(ex.getMessage()));
        }
    }
    
    @Test
    public void RequestProcessorDoesNotThrowsIfCorrectLineEnding()
    {
        try
        {
            SocketStubComplete socketStub = new SocketStubComplete();
            socketStub.setBytestoRead(10);
            String result = _unitUnderTest.readLine(socketStub);
            
            assertTrue("XXXXXXXXXX".equals(result));
        }
        catch (IOException | ProtocolException ex)
        {
            fail("Thows exception: " + ex.getMessage());
        }
    }
    
    @Test
    public void RequestProcessorHandlesEmptyLine()
    {
        try
        {
            SocketStubEmptyLine socketStub = new SocketStubEmptyLine();
            socketStub.setBytestoRead(10);
            String result = _unitUnderTest.readLine(socketStub);
            
            assertTrue("XXXXXXXXXX".equals(result));
            
            result = _unitUnderTest.readLine(socketStub);
            assertTrue(result.isEmpty());
        }
        catch (IOException | ProtocolException ex)
        {
            fail("Thows exception: " + ex.getMessage());
        }
    }
    
    @Test
    public void AddsHostToContext()
    {
        IParser parser = new Parser();
        _unitUnderTest = new RequestBuilder(parser);
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nHost: 123\r\n\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        assertTrue("123".equals(context.request().host()));
    }
    
    @Test
    public void HostHeaderNotInHeadersList()
    {
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nHost: 123\r\n\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        
        assertTrue(context.request().header("host") == null);
    }
    
    @Test
    public void BadRequestIfMoreThanOneHostHeader()
    {
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nHost: 123\r\nHost: 456\r\n\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        
        assertTrue(context.response().status() == 400);
    }
    
    @Test
    public void BadRequestIfMissingHostHeader()
    {
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nheader1: 123\r\nheader2: 456\r\n\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        
        assertTrue(context.response().status() == 400);
    }
    
    @Test
    public void BadRequestIfContentLengthNaN()
    {
        IParser parser = new Parser();
        _unitUnderTest = new RequestBuilder(parser);
        SocketStubComplete socketStub = new SocketStubComplete();
        socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nHost: 123\r\nContent-length: ab\r\n\r\nhello\r\n");
        HttpContext context = _unitUnderTest.ProcessRequest(socketStub);
        
        assertTrue(context.response().status() == 400);
    }
    
    @Test
    public void ContentLengthIgnoredForTransferEncoding()
    {
        IParser parser = new Parser();
        _unitUnderTest = new RequestBuilder(parser);
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
            IParser parser = new Parser();
            _unitUnderTest = new RequestBuilder(parser);
            SocketStubComplete socketStub = new SocketStubComplete();
            socketStub.setMessageToRead("\"POST /my/request HTTP/1.1\r\nHost: 123\r\nContent-length: 5\r\n\r\nhello\r\n");
            HttpContext context = _unitUnderTest.ProcessRequest(socketStub);

            assertTrue("hello".equals(context.request().body().asString("UTF-8")));
        }
        catch (Exception ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
}
