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
package Tests.Protocol.Processing;

import Network.Wrappers.ISocketChannel;
import Protocol.Models.Header;
import Protocol.Models.RequestBody;
import Protocol.Parsers.ProtocolException;
import Request.Processing.EncodingReaders;
import Request.Processing.TransferEncodingProcessor;
import Server.IHeader;
import Tests.Stubs.Network.Readers.*;
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
public class TransferEncodingProcessorTests
{
    private TransferEncodingProcessor _unitUnderTest;
    private final String Encoding = "UTF-8";
    
    @Before
    public void setup()
    {
        EncodingReaders readers = new EncodingReaders();
        readers.addReader(new ChunkedReaderStub(Encoding));
        readers.addReader(new DeflateReaderStub(Encoding));
        readers.addReader(new GzipReaderStub(Encoding));
        readers.addReader(new IdentityReaderStub(Encoding));
        
        _unitUnderTest = new TransferEncodingProcessor(readers);
    }
    
    @Test
    public void callsSingleDecoder()
    {
        
        try
        {
            IHeader header = new Header("Transfer-encoding", "gzip");
            String testData = "Data to test";
            ISocketChannel channel = new SocketStubBinary();

            RequestBody result = _unitUnderTest.decode(channel, header, testData.getBytes(Encoding));
            
            String resultStr = result.asString(Encoding);
            
            assertTrue("Data to test gzip".equals(resultStr));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void callsMultipleDecodersInReverseOrder()
    {
        
        try
        {
            IHeader header = new Header("Transfer-encoding", "identity,gzip,chunked");
            String testData = "Data to test";
            ISocketChannel channel = new SocketStubBinary();

            RequestBody result = _unitUnderTest.decode(channel, header, testData.getBytes(Encoding));
            
            String resultStr = result.asString(Encoding);
            
            assertTrue("Data to test chunked gzip identity".equals(resultStr));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void CallsChunkedWithChannel()
    {
        try
        {
            IHeader header = new Header("Transfer-encoding", "identity,chunked");
            String testData = "Data to test";
            SocketStubComplete channel = new SocketStubComplete();

            RequestBody result = _unitUnderTest.decode(channel, header, testData.getBytes(Encoding));
            
            String resultStr = result.asString(Encoding);
            
            assertTrue("Data to test chunked identity".equals(resultStr));
            assertTrue(channel.NumReads == 1);
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
}
