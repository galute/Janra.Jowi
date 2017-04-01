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
package Network.Wrappers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 *
 * @author jmillen
 */
public class SocketChannelWrapper implements ISocketChannel
{
    SocketChannel _socket;
    Charset _charset=Charset.forName("ISO-8859-1");
    
    public SocketChannelWrapper(SocketChannel socket)
    {
        _socket = socket;
    }
    
    public SocketChannel getChannel()
    {
        return _socket;
    }
    
    @Override
    public void setNonBlocking(Boolean flag) throws IOException
    {
        check();
        _socket.configureBlocking(flag); 
    }
    
    @Override
    public Integer read(ByteBuffer buffer) throws IOException
    {
        check();
        
        return _socket.read(buffer);
    }
    
    @Override
    public Integer write(ByteBuffer buffer) throws IOException
    {
        check();
        
        return _socket.write(buffer);
    }
    
    @Override
    public void close() throws IOException
    {
        if (isDisposed())
        {
            return;
        }
        _socket.close();
        _socket = null;
    }
    
    private void check() throws IOException
    {
        if (isDisposed())
        {
            throw new IOException("Socket is disposed");
        }
    }
    
    private Boolean isDisposed()
    {
        return (_socket == null);
    }
}
