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
import Tests.Stubs.Parsers.ParserStub;
import java.io.IOException;
import java.nio.ByteBuffer;
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
            ByteBuffer bbuffer = ByteBuffer.allocate(10);
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
}
