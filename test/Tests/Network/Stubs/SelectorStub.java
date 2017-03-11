/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests.Network.Stubs;

import Network.Wrappers.ISelector;
import Network.Wrappers.ISelectorKeys;
import Network.Wrappers.IServerSocketChannel;
import Network.Wrappers.ISocketChannel;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;

/**
 *
 * @author jmillen
 */
public class SelectorStub implements ISelector
{
    public Integer _registeredForAccept = 0;
    public Integer _registeredForRead = 0;
    public Integer _numKeysToSelect = 0;
    public Boolean _setAcceptable = false;
    public Boolean _setReadable = false;
    
    @Override
    public void RegisterForAccepts(IServerSocketChannel serverChannel) throws ClosedChannelException, IOException
    {
        _registeredForAccept++;
    }

    @Override
    public void RegisterForReads(ISocketChannel serverChannel) throws ClosedChannelException, IOException
    {
        _registeredForRead++;
    }
    
    @Override
    public ISelectorKeys WaitForRequests() throws IOException
    {
        return new SelectorKeysStub(_numKeysToSelect, _setAcceptable, _setReadable);
    }

    @Override
    public void Close() throws IOException
    {
        // Do nothing
    }
}
