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
