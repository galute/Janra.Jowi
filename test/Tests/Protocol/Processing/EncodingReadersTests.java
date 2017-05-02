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
import Protocol.Parsers.ProtocolException;
import Request.Processing.EncodingReaders;
import Tests.Stubs.Network.Readers.*;
import Tests.Stubs.Network.SocketStubBinary;
import java.io.IOException;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class EncodingReadersTests
{
    private EncodingReaders _unitUnderTest;
    private final String Encoding = "UTF-8";
    private SocketStubBinary _channel;
    
    @Before
    public void setup()
    {
        _unitUnderTest = new EncodingReaders();
        _unitUnderTest.addReader(new ChunkedReaderStub(Encoding));
        _unitUnderTest.addReader(new DeflateReaderStub(Encoding));
        _unitUnderTest.addReader(new GzipReaderStub(Encoding));
        _unitUnderTest.addReader(new IdentityReaderStub(Encoding));
        _channel = new SocketStubBinary();
    }
    
    @Test
    public void ThrowsIfNoReader()
    {
        try
        {
            String testData = "Data to test";

            _unitUnderTest.decode("foobar", testData.getBytes(Encoding), _channel);
            
            fail("Expected exception not thrown");
        }
        catch (ProtocolException | IOException ex)
        {
            assertTrue(ex instanceof ProtocolException);
            assertTrue("foobar transfer encoding not supported.".equals(ex.getMessage()));
            assertTrue(((ProtocolException)ex).ResponseStatus == 501);
        }
    }
    
    @Test
    public void CallsReader()
    {
        try
        {
            String testData = "Data to test";

            byte[] result = _unitUnderTest.decode("gzip", testData.getBytes(Encoding), _channel);
            
            String resultStr = new String(result, Encoding);
            
            assertTrue("Data to test gzip".equals(resultStr));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void CanCallMultipleReaders()
    {
        try
        {
            String testData = "Data to test";

            byte[] result = _unitUnderTest.decode("gzip", testData.getBytes(Encoding), _channel);
            result = _unitUnderTest.decode("deflate", result, _channel);
            result = _unitUnderTest.decode("identity", result, _channel);
            
            String resultStr = new String(result, Encoding);
            
            assertTrue("Data to test gzip deflate identity".equals(resultStr));
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
}
