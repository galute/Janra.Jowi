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
package Tests.Network;

import Network.Handlers.ChunkedReader;
import Protocol.Models.RequestBody;
import Protocol.Parsers.ProtocolException;
import Tests.Stubs.Network.*;
import java.io.IOException;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class ChunkedReaderTests
{
    private ChunkedReader _unitUnderTest;
    private SocketStubComplete _channelComplete;
    
    @Before
    public void setup()
    {
        _channelComplete = new SocketStubComplete();
        _unitUnderTest = new ChunkedReader();
    }
    
    @Test
    public void ReadsLengthWithoutExtensions()
    {
        try
        {
            _channelComplete.setMessageToRead("1E\r\naaaaaaaaaaaaaaabbbbbbbbbbbbbbb\r\n0\r\n\r\n");
            RequestBody result = _unitUnderTest.getBody(_channelComplete);
            
            assertTrue(result.raw().length == 32);
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ReadsLengthWithExtensions()
    {
        try
        {
            _channelComplete.setMessageToRead("1E;foobar\r\naaaaaaaaaaaaaaabbbbbbbbbbbbbbb\r\n0\r\n\r\n");
            RequestBody result = _unitUnderTest.getBody(_channelComplete);
            
            assertTrue(result.raw().length == 32);
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ThrowsIfLengthBiggerThanMaxInteger()
    {
        try
        {
            _channelComplete.setMessageToRead("8FFFFFFF;foobar\r\naaaaaaaaaaaaaaabbbbbbbbbbbbbbb\r\n0\r\n\r\n");
            _unitUnderTest.getBody(_channelComplete);
            
            fail("Expected exception not thrown");
        }
        catch (ProtocolException | IOException ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertTrue("Chunk size too big".equals(ex.getMessage()));
        }
    }
    
    @Test
    public void ReadsChunkIncludingLineEnds()
    {
        try
        {
            _channelComplete.setMessageToRead("8\r\nfoo\r\nbar\r\n0\r\n\r\n");
            RequestBody result = _unitUnderTest.getBody(_channelComplete);
            
            assertTrue(result.raw().length == 10);
            assertTrue("foo\r\nbar".equals(result.asString("UTF-8")));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void WaitsForAllData()
    {
        SocketStubGaps channel = new SocketStubGaps();
        try
        {
            channel.setMessageToRead("11;stuff\r\nfoo\r\nbar stretch \r\n0\r\n\r\n");
            RequestBody result = _unitUnderTest.getBody(channel);
            
            assertTrue(result.raw().length == 19);
            assertTrue("foo\r\nbar stretch ".equals(result.asString("UTF-8")));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ThrowsIfTimeout()
    {
        try
        {
            _channelComplete.setMessageToRead("8\r\nfoobar");
            _unitUnderTest.getBody(_channelComplete);
            
            fail("Expected exception not thrown");
        }
        catch (ProtocolException | IOException ex)
        {
            assertTrue(ex instanceof IOException);
            assertTrue("Timeout after max retries of 5".equals(ex.getMessage()));
        }
    }
    
    @Test
    public void ThrowsIfChunkDataMissing()
    {
        SocketStubIncomplete channel = new SocketStubIncomplete();
        try
        {
            _unitUnderTest.getBody(channel);
            
            fail("Expected exception not thrown");
        }
        catch (ProtocolException | IOException ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertTrue("Unable to read chunk, incomplete data".equals(ex.getMessage()));
        }
    }
    
    @Test
    public void ReadsUntilEmptyChunk()
    {
        try
        {
            _channelComplete.setMessageToRead("8\r\nfoo\r\nbar\r\n14\r\n and another foo bar\r\n0\r\n\r\n");
            RequestBody result = _unitUnderTest.getBody(_channelComplete);
            
            assertTrue(result.raw().length == 30);
            assertTrue("foo\r\nbar and another foo bar".equals(result.asString("UTF-8")));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
}
