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
    public Boolean _setWriteable = false;
    public long _timeout = 0;
    public SelectorKeysStub _returnedKeys = null;
    
    @Override
    public void registerForAccepts(IServerSocketChannel serverChannel) throws ClosedChannelException, IOException
    {
        if (serverChannel == null)
        {
            throw new IOException("SelectorStub: Tried to register For Accept with null ServerSocketChannel");
        }
        _registeredForAccept++;
    }

    @Override
    public void registerForReads(ISocketChannel socketChannel) throws ClosedChannelException, IOException
    {
        if (socketChannel == null)
        {
            throw new IOException("SelectorStub: Tried to register for Read with null SocketChannel");
        }
        _registeredForRead++;
    }
    
    @Override
    public ISelectorKeys waitForRequests(long timeout) throws IOException
    {
        _timeout = timeout;
        
        if (_numKeysToSelect > 0)
        {
            Integer numKeys = _numKeysToSelect;
            _numKeysToSelect = 0;
            
            _returnedKeys = new SelectorKeysStub(numKeys, _setAcceptable, _setReadable, _setWriteable);
            
            return _returnedKeys;
        }
        
        try
        {
            Thread.sleep(timeout);
        }
        catch (Exception ex)
        {
            throw new IOException("SelectorStub: Timeout interrupted");
        }
        
        return null;
    }

    @Override
    public void close() throws IOException
    {
        // Do nothing
    }
}
