/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
