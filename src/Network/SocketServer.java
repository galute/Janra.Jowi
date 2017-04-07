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
package Network;

import Network.Wrappers.*;
import java.io.IOException;

/**
 *
 * @author jmillen
 */
public class SocketServer implements ISocketServer
{
    IServerSocketChannel _serverSocket;
    ISelector _selector;
    
    public SocketServer(IServerSocketChannel serverSocket, ISelector selector)
    {
        _serverSocket = serverSocket;
        _selector = selector;
    }
    
    @Override
    public void Configure(Integer port) throws IOException
    {
        Check();
        _serverSocket.setNonBlocking(Boolean.TRUE);
        _serverSocket.bind(port);
        _selector.registerForAccepts(_serverSocket);
    }
    
    @Override
    public ISelectorKeys Start(long timeout) throws IOException
    {
        Check();
        return _selector.waitForRequests(timeout);
    }
    
    @Override
    public ISocketChannel Accept(ISelectorKey key) throws IOException
    {
        Check();
        
        ISocketChannel socket = key.getChannel();
        
        _selector.registerForReads(socket);

        return socket;
    }
    
    @Override
    public void Close() throws IOException
    {
        if (isDisposed())
        {
            return;
        }
        _selector.close();
        _serverSocket.close();
        _selector = null;
        _serverSocket = null;
    }
    
    private void Check() throws IOException
    {
        if (isDisposed())
        {
            throw new IOException("Socket is closed");
        }
    }
    
    private Boolean isDisposed()
    {
        return _serverSocket == null;
    }
}
