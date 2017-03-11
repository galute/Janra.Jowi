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
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

/**
 *
 * @author jmillen
 */
public class ServerSocketChannelWrapper implements IServerSocketChannel
{
    
    ServerSocketChannel _serverSocketChannel;
    
    public ServerSocketChannelWrapper() throws IOException
    {
        _serverSocketChannel = ServerSocketChannel.open();
    }
    
    @Override
    public void SetNonBlocking(Boolean flag) throws IOException
    {
        Check();
        _serverSocketChannel.configureBlocking(flag); 
    }
    
    @Override
    public void Bind(Integer port) throws IOException
    {
        Check();
        _serverSocketChannel.socket().bind(new InetSocketAddress(port));
    }
    
    @Override
    public void Close() throws IOException
    {
        if (isDisposed())
        {
            return;
        }

        _serverSocketChannel.close();
        _serverSocketChannel = null;
    }
    
    public ServerSocketChannel GetChannel()
    {
        return _serverSocketChannel;
    }
    
    private void Check() throws IOException
    {
        if (isDisposed())
        {
            throw new IOException("Server Socket is disposed");
        }
    }

    private Boolean isDisposed()
    {
        return (_serverSocketChannel == null);
    }
}
