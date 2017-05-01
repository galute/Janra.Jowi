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
package Tests.Stubs.Network;

import Network.Wrappers.ISocketChannel;
import java.io.IOException;
import java.nio.ByteBuffer;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class SocketStubBinary implements ISocketChannel
{
    public byte[] DataForReading = new byte[0];
    private int readIndex = 0;
    
    @Override
    public void setNonBlocking(Boolean flag) throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer read(ByteBuffer buffer) throws IOException
    {
        Integer bytes = buffer.remaining();
        
        if (bytes < DataForReading.length - readIndex + 1)
        {
            byte[] toBeRead = new byte[bytes];
            System.arraycopy(DataForReading, readIndex, toBeRead, 0, bytes);
            buffer.put(toBeRead);
            readIndex +=bytes;
            return bytes;
        }
        else
        {
            int remainingSz = DataForReading.length - readIndex;
            
            if (remainingSz == 1)
            {
                return -1; // End of stream
            }
            byte[] toBeRead = new byte[remainingSz];
            System.arraycopy(DataForReading, readIndex, toBeRead, 0, remainingSz);
            buffer.put(toBeRead);
            readIndex = DataForReading.length - 1;
            return remainingSz;
        }
        
    }

    @Override
    public Integer write(ByteBuffer buffer) throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    // Make sure the above read() code does as it's supposed to
    @Test
    public void ReadTest()
    {
        try
        {
            DataForReading = "This is a test".getBytes("UTF-8");
            ByteBuffer buffer = ByteBuffer.allocate(5);
            
            Integer result = read(buffer);
            
            assertTrue(result == 5);
            assertTrue(buffer.hasArray());
            assertFalse(buffer.hasRemaining());
            
            String resultStr = new String(buffer.array());
            assertTrue("This ".equals(resultStr));
            
            buffer = ByteBuffer.allocate(12);
            
            result = read(buffer);
            
            assertTrue(result == 9);
            assertTrue(buffer.hasArray());
            assertTrue(buffer.hasRemaining());
            
            byte[] arr = new byte[9];
            System.arraycopy(buffer.array(), 0, arr, 0, 9);
            
            resultStr = new String(arr, "UTF-8");
            assertTrue("is a test".equals(resultStr));
            
            buffer = ByteBuffer.allocate(5);
            
            result = read(buffer);
            
            assertTrue(result == -1);
            
        } 
        catch (IOException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
}
