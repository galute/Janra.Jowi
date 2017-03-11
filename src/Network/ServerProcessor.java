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

import Network.Wrappers.ISelectorKey;
import Network.Wrappers.ISelectorKeys;
import Network.Wrappers.ISocketChannel;
import java.io.IOException;

/**
 *
 * @author jmillen
 */
public class ServerProcessor
{
    private final IServer _server;
    private Boolean _stop = false;
    
    public ServerProcessor(IServer server) throws IOException
    {
        _server = server;
    }
    
    public void Start(Integer port) throws IOException
    {
        _server.Configure(port);
        
        while (!_stop)
        {
            ISelectorKeys keys = _server.Start();
            
            ISelectorKey key = keys.GetNext();
            
            if (key.IsAcceptable())
            {
                ISocketChannel socketChannel = _server.Accept(key);
            }
        }
    }
    
    public void Stop()
    {
        _stop = true;
    }
}
