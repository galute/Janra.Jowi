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

import Network.Wrappers.ISelectorKey;
import Network.Wrappers.ISocketChannel;
import java.io.IOException;

/**
 *
 * @author jmillen
 */
public class SelectorKeyStub implements ISelectorKey
{
    public Boolean IsAcceptable = false;
    public Boolean IsReadable = false;
    public Boolean IsCancelled = false;
    public SocketStub SocketStub = null;
    
    public SelectorKeyStub(Boolean isAcceptable, Boolean isReadable)
    {
        IsAcceptable = isAcceptable;
        IsReadable = isReadable;
        SocketStub = new SocketStub();
    }
    
    @Override
    public Boolean isAcceptable()
    {
        return IsAcceptable;
    }

    @Override
    public Boolean isReadable()
    {
        return IsReadable;
    }

    @Override
    public ISocketChannel getChannel() throws IOException
    {
        return SocketStub;
    }

    @Override
    public void cancel()
    {
        IsCancelled = true;
    }
    
}
