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

import Network.Handlers.ContentLengthReader;
import Protocol.Models.Header;
import Protocol.Models.RequestBody;
import Protocol.Parsers.ProtocolException;
import Server.IHeader;
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
public class ContentLengthReaderTests
{
    private ContentLengthReader _unitUnderTest;
    private SocketStubComplete _channelComplete;
    private SocketStubInterrupting _channelInterrupting;
    private SocketStubIncomplete _channelIncomplete;
    
    @Before
    public void setup()
    {
        _channelComplete = new SocketStubComplete();
        _channelInterrupting = new SocketStubInterrupting();
        _channelIncomplete = new SocketStubIncomplete();
    }
    
    private void GivenContentLength(Integer length) throws ProtocolException
    {
        IHeader header = new Header("content-length", length.toString());
        
        _channelComplete.setBytestoRead(length);
        
        _unitUnderTest = new ContentLengthReader(header);
    }
    
    private void GivenDripFeedingSocket(Integer totalLength, Integer perReadSize) throws ProtocolException
    {
        IHeader header = new Header("content-length", totalLength.toString());
        
        _channelInterrupting.setBytestoRead(totalLength);
        _channelInterrupting.setChunkSize(perReadSize);
        
        _unitUnderTest = new ContentLengthReader(header);
    }
    
    private void GivenNoIncomingData(Integer expected) throws ProtocolException
    {
        IHeader header = new Header("content-length", expected.toString());
        
        _unitUnderTest = new ContentLengthReader(header);
    }
    
    @Test
    public void ReadsAllContentInOneGoIfAvailable()
    {
        try
        {
            GivenContentLength(10);
            
            RequestBody result = _unitUnderTest.getBody(_channelComplete);
            
            String resultStr = result.asString("UTF-8");
            
            assertTrue(result.raw().length == 12);
            assertTrue("XXXXXXXXXX\r\n".equals(resultStr));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void AttemptsToReadAllDataWhenIncomplete()
    {
        try
        {
            GivenDripFeedingSocket(10, 2);
            
            RequestBody result = _unitUnderTest.getBody(_channelInterrupting);
            
            String resultStr = result.asString("UTF-8");
            
            assertTrue(result.raw().length == 12);
            assertTrue("XXXXXXXXXX\r\n".equals(resultStr));
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
            GivenNoIncomingData(10);
            
            _unitUnderTest.getBody(_channelComplete);
            
            fail("Expected Exception not thrown");
        }
        catch (ProtocolException | IOException ex)
        {
            assertTrue(_channelComplete.NumReads == 5);
            assertTrue(ex instanceof IOException);
            assertTrue("Incomplete data read".equals(ex.getMessage()));
        }
    }
    
    @Test
    public void ThrowsWhenEndOfStreamBeforeAllExpectedData()
    {
        try
        {
            GivenNoIncomingData(10);
            
            _unitUnderTest.getBody(_channelIncomplete);
            
            fail("Expected Exception not thrown");
        }
        catch (ProtocolException | IOException ex)
        {
            assertTrue(ex instanceof IOException);
            assertTrue("Incomplete data read".equals(ex.getMessage()));
        }
    }
}
