/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        ServerSocketChannelWrapper serverWrapper = (ServerSocketChannelWrapper)serverChannel;
        
        if (serverWrapper == null)
        {
            throw new IOException("ServerSocketChannel is null");
        }
        
        if (serverWrapper.GetChannel() == null)
        {
            throw new IOException("Server socket is disposed");
        }
        
        serverWrapper.GetChannel().register(_selector, SelectionKey.OP_READ);
    }
    
    @Override
    public ISelectorKeys WaitForRequests() throws IOException
    {
        Check();
        _selector.select();
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
