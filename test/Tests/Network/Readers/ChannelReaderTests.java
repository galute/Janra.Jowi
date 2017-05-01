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

import Network.Readers.ChannelReader;
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
public class ChannelReaderTests
{
    private ChannelReader _unitUnderTest;
    private SocketStubComplete _channelComplete;
    private SocketStubInterrupting _channelInterrupting;
    private SocketStubBinary _channelBinary;
    
    @Before
    public void setup()
    {
        _unitUnderTest = new ChannelReader();
        _channelComplete = new SocketStubComplete();
        _channelInterrupting = new SocketStubInterrupting();
        _channelBinary = new SocketStubBinary();
    }
    
    private void givenContentLength(Integer length, String encoding) throws ProtocolException
    {
        _channelComplete.setBytestoRead(length);
        _channelComplete.Encoding = encoding;
    }
    
    private void givenDataToRead(String dataToReadBack, String encoding) throws UnsupportedEncodingException
    {
        _channelBinary.DataForReading = dataToReadBack.getBytes(encoding);
        _channelComplete.setMessageToRead(dataToReadBack);
        _channelComplete.Encoding = encoding;
    }
    
    private void GivenDripFeedingSocket(Integer totalLength, Integer perReadSize, String encoding) throws ProtocolException
    {
        _channelInterrupting.setBytestoRead(totalLength);
        _channelInterrupting.setChunkSize(perReadSize);
        _channelInterrupting.Encoding = encoding;
    }
    
    @Test
    public void CorrectlyReadsLine()
    {
        try
        {
            givenContentLength(10, "ISO-8859-1");
            
            byte[] result = _unitUnderTest.readLine(_channelComplete);
            
            String resultStr = new String(result,"ISO-8859-1");
            
            assertTrue(result.length == 10);
            assertTrue("XXXXXXXXXX".equals(resultStr));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void CorrectlyReadsAllData()
    {
        String testMessage = "this is\r\n a test";
        try
        {
            givenDataToRead(testMessage, "ISO-8859-1");
            
            byte[] result = _unitUnderTest.readBytes(_channelBinary, testMessage.length());
            
            String resultStr = new String(result,"ISO-8859-1");
            
            assertTrue(result.length == testMessage.length());
            assertTrue(testMessage.equals(resultStr));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void WaitsForSlowData()
    {
        try
        {
            GivenDripFeedingSocket(10, 2, "ISO-8859-1");
            
            byte[] result = _unitUnderTest.readBytes(_channelInterrupting, 10);
            
            String resultStr = new String(result,"ISO-8859-1");
            
            assertTrue(result.length == 10);
            assertTrue("XXXXXXXXXX".equals(resultStr));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void TimesOutIfNoMoreData()
    {
        try
        {
            _unitUnderTest.readBytes(_channelComplete, 10);
            
            fail("Expected Exception not thrown");
        }
        catch (ProtocolException | IOException ex)
        {
            assertTrue(_channelComplete.NumReads == 5);
            assertTrue(ex instanceof IOException);
            assertTrue("Timeout after max retries of 5".equals(ex.getMessage()));
        }
    }
    
    @Test
    public void ThrowsWhenEndOfStreamBeforeAllExpectedData()
    {
        try
        {
            _unitUnderTest.readBytes(_channelBinary, 10);
            
            fail("Expected Exception not thrown");
        }
        catch (ProtocolException | IOException ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertTrue("Unexpected end of data".equals(ex.getMessage()));
        }
    }
    
    @Test
    public void ThrowsWhenLineEndingIncorrect()
    {
        try
        {
            givenDataToRead("This is a bad line\n\r", "ISO-8859-1");
            
            _unitUnderTest.readLine(_channelComplete);
            
            fail("Expected Exception not thrown");
        }
        catch (ProtocolException | IOException ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertTrue("Unexpected new line character".equals(ex.getMessage()));
        }
    }
}
