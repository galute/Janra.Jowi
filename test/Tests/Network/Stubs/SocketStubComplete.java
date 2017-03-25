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
package Tests.Network.Stubs;

import Network.Wrappers.ISocketChannel;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author jmillen
 */
public class SocketStubComplete implements ISocketChannel
{
    public Boolean IsNonBlocking = false;
    public Integer NumReads = 0;
    private String bytesToRead = "";
    
    @Override
    public Integer read(ByteBuffer buffer) throws IOException
    {
        NumReads++;
        Integer bytes = buffer.remaining();
        
        String toReturn = "";
        
        if (bytes < bytesToRead.length())
        {
            toReturn = bytesToRead.substring(0, (buffer.remaining()));
            bytesToRead = bytesToRead.substring(buffer.remaining());
        }
        else
        {
            toReturn = bytesToRead;
            bytesToRead = "";
            bytes = toReturn.length();
        }
        
        buffer.put(toReturn.getBytes());
        
        return bytes;
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

    @Override
    public void setNonBlocking(Boolean flag) throws IOException
    {
        IsNonBlocking = true;
    }
    
    public void setBytestoRead(Integer bytes)
    {
        bytesToRead = new String(new char[bytes]).replace("\0", "X");
        bytesToRead = bytesToRead.concat("\r\n");
    }
}
