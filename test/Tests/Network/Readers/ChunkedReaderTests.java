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
package Tests.Network.Readers;

import Network.Readers.ChunkedReader;
import Protocol.Parsers.ProtocolException;
import Tests.Stubs.Network.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    
    private void givenDataToRead(String dataToReadBack, String encoding) throws UnsupportedEncodingException
    {
        _channelComplete.setMessageToRead(dataToReadBack);
        _channelComplete.Encoding = encoding;
    }
    
    @Test
    public void ReadsLengthWithoutExtensions()
    {
        try
        {
            givenDataToRead("1E\r\naaaaaaaaaaaaaaabbbbbbbbbbbbbbb\r\n0\r\n\r\n", "ISO-8859-1");
            byte[] result = _unitUnderTest.processData(new byte[0], _channelComplete);
            
            assertTrue(result.length == 32);
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
            byte[] result = _unitUnderTest.processData(new byte[0], _channelComplete);
            
            assertTrue(result.length == 32);
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
            _unitUnderTest.processData(new byte[0], _channelComplete);
            
            fail("Expected exception not thrown");
        }
        catch (ProtocolException | IOException ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertTrue("Chunk size too big or invalid".equals(ex.getMessage()));
        }
    }
    
    @Test
    public void ReadsChunkIncludingLineEnds()
    {
        try
        {
            _channelComplete.setMessageToRead("8\r\nfoo\r\nbar\r\n0\r\n\r\n");
            _channelComplete.Encoding = "ISO-8859-1";
            byte[] result = _unitUnderTest.processData(new byte[0], _channelComplete);
            
            assertTrue(result.length == 10);
            assertTrue("foo\r\nbar\r\n".equals(new String(result, "UTF-8")));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ReadsUntilEmptyChunk()
    {
        try
        {
            _channelComplete.setMessageToRead("8\r\nfoo\r\nbar\r\n14\r\n and another foo bar\r\n0\r\n\r\n");
            byte[] result = _unitUnderTest.processData(new byte[0], _channelComplete);
            
            assertTrue(result.length == 30);
            assertTrue("foo\r\nbar and another foo bar\r\n".equals(new String(result, "UTF-8")));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
}
