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
    public void RegisterForAccepts(IServerSocketChannel serverChannel) throws ClosedChannelException, IOException
    {
        Check();
        ServerSocketChannelWrapper serverWrapper = (ServerSocketChannelWrapper)serverChannel;
        
        if (serverWrapper == null)
        {
            throw new IOException("ServerSocketChannel is null");
        }
        
        if (serverWrapper.GetChannel() == null)
        {
            throw new IOException("Server socket is disposed");
        }
        
        serverWrapper.GetChannel().register(_selector, SelectionKey.OP_ACCEPT);
    }
    
    @Override
    public void RegisterForReads(ISocketChannel serverChannel) throws ClosedChannelException, IOException
    {
        Check();
        SocketChannelWrapper serverWrapper = (SocketChannelWrapper)serverChannel;
        
        if (serverWrapper == null)
        {
            throw new IOException("SocketChannel is null");
        }
        
        if (serverWrapper.GetChannel() == null)
        {
            throw new IOException("Server socket is disposed");
        }
        
        serverWrapper.GetChannel().register(_selector, SelectionKey.OP_READ);
    }
    
    @Override
    public ISelectorKeys WaitForRequests(long timeout) throws IOException
    {
        Check();
        _selector.select(timeout);
        Set keys = _selector.selectedKeys();
        
        return new SelectorKeys(keys);
    }
    
    @Override
    public void Close() throws IOException
    {
        if (isDisposed())
        {
            return;
        }
        _selector.wakeup();
        _selector.close();
        _selector = null;
    }
    
    private void Check() throws IOException
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
