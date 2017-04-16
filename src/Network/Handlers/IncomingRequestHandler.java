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
package Network.Handlers;

import Network.Factories.RequestHandlerFactory;
import Network.ISocketServer;
import Network.Wrappers.ISelectorKey;
import Network.Wrappers.ISelectorKeys;
import Network.Wrappers.ISocketChannel;
import Request.Processing.IMarshaller;
import Server.IConfiguration;
import Utilities.ILauncher;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jmillen
 */
public class IncomingRequestHandler implements Runnable
{
    private final ISocketServer _server;
    private final ILauncher _launcher;
    private final IMarshaller _marshaller;
    private volatile boolean _stop = false;
    private final long _timeout;
    private final Integer _port;
    
    
    public IncomingRequestHandler(ISocketServer server, ILauncher launcher, Integer port, IConfiguration config, IMarshaller marshaller) throws IOException
    {
        _server = server;
        _timeout = config.timeout();
        _port = port;
        _launcher = launcher;    
        _marshaller = marshaller;
    }
    
    @Override
    public void run()
    {
        try
        {
            _server.Configure(_port);

            while (!_stop)
            {
                ISelectorKeys keys = _server.Start(_timeout);
                
                if (keys == null)
                {
                    continue;
                }

                Boolean hasMoreKeys = true;
                while (hasMoreKeys)
                {
                    ISelectorKey key = keys.getNext();

                    if (key == null)
                    {
                        System.out.print("No keys\n");
                        hasMoreKeys = false;
                        continue;
                    }

                    if (key.isAcceptable())
                    {
                        System.out.print("Accepting ********************\n");

                        ISocketChannel channel = _server.Accept(key);
                        _launcher.launch(RequestHandlerFactory.Create(channel, _marshaller, _timeout));
                    }
                    else
                    {
                        System.out.print("Cancelling");
                        key.cancel();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(IncomingRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            Stop();
            Thread.currentThread().interrupt();
        }
        finally
        {
            try
            {
                _server.Close();
            } 
            catch (IOException ex)
            {
                Logger.getLogger(IncomingRequestHandler.class.getName()).log(Level.SEVERE, "Exiting: ", ex);
            }
        }
    }
    
    public void Stop()
    {
        _stop = true;
    }
    
    public Boolean isStopped()
    {
        return _stop;
    }
}
//java.nio.channels.IllegalBlockingModeException
//	at java.nio.channels.spi.AbstractSelectableChannel.configureBlocking(AbstractSelectableChannel.java:293)
//	at Network.Wrappers.SocketChannelWrapper.setNonBlocking(SocketChannelWrapper.java:47)
//	at Network.Handlers.IncomingRequestHandler.run(IncomingRequestHandler.java:80)
//	at Server.Server.Start(Server.java:46)
//	at Examples.Basic.Program.main(Program.java:38)