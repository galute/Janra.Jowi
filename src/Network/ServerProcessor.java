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
public class ServerProcessor implements Runnable
{
    private final IServer _server;
    private volatile boolean _stop = false;
    private final long _timeout;
    private final Integer _port;
    
    
    public ServerProcessor(IServer server, Integer port, long timeout) throws IOException
    {
        _server = server;
        _timeout = timeout;
        _port = port;
    }
    
    @Override
    public void run()
    {
        int x = 0;
        try
        {
            _server.Configure(_port);

            while (!_stop)
            {
                ISelectorKeys keys = _server.Start(_timeout);

                ISelectorKey key = keys.GetNext();

                if (key.IsAcceptable())
                {
                    ISocketChannel socketChannel = _server.Accept(key);
                }
            }
        }
        catch (Exception ex)
        {
            Thread.currentThread().interrupt();
        }
    }
    
    public void Stop()
    {
        _stop = true;
    }
}
