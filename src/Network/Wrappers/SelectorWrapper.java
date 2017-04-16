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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

/**
 *
 * @author jmillen
 */
public class SelectorWrapper implements ISelector
{
    public Selector _selector;
    
    public SelectorWrapper() throws IOException
    {
        _selector = Selector.open();
    }
    
    @Override
    public void registerForAccepts(IServerSocketChannel serverChannel) throws ClosedChannelException, IOException
    {
        check();
        ServerSocketChannelWrapper serverWrapper = (ServerSocketChannelWrapper)serverChannel;
        
        if (serverWrapper == null)
        {
            throw new IOException("ServerSocketChannel is null");
        }
        
        if (serverWrapper.getChannel() == null)
        {
            throw new IOException("Server socket is disposed");
        }
        
        serverWrapper.getChannel().register(_selector, SelectionKey.OP_ACCEPT);
    }
    
    @Override
    public void registerForReads(ISocketChannel socketChannel) throws ClosedChannelException, IOException
    {
        check();
        SocketChannelWrapper socketWrapper = (SocketChannelWrapper)socketChannel;
        
        if (socketWrapper == null)
        {
            throw new IOException("SocketChannel is null");
        }
        
        if (socketWrapper.getChannel() == null)
        {
            throw new IOException("Server socket is disposed");
        }
        socketWrapper.setNonBlocking(Boolean.FALSE);
        socketWrapper.getChannel().register(_selector, SelectionKey.OP_READ);
    }
    
    @Override
    public ISelectorKeys waitForRequests(long timeout) throws IOException
    {
        check();
        _selector.select(timeout);
        Set keys = _selector.selectedKeys();
        
        return new SelectorKeys(keys);
    }
    
    @Override
    public void close() throws IOException
    {
        if (isDisposed())
        {
            return;
        }
        _selector.wakeup();
        _selector.close();
        _selector = null;
    }
    
    private void check() throws IOException
    {
        if (isDisposed())
        {
            throw new IOException("Selector is disposed");
        }
    }
    
    private Boolean isDisposed()
    {
        return (_selector == null);
    }
}
