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

import Network.Factories.IRequestHandlerFactory;
import Network.ISocketServer;
import Network.Wrappers.*;
import Protocol.Models.HttpResponse;
import Request.Processing.IMarshaller;
import Request.Processing.ISendResponse;
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
    private final IRequestHandlerFactory _factory;
    private final ISocketServer _server;
    private final ILauncher _launcher;
    private final IMarshaller _marshaller;
    private final ISendResponse _responder;
    private volatile boolean _stop = false;
    private final long _timeout;
    private final Integer _port;
    
    
    public IncomingRequestHandler(IRequestHandlerFactory factory, ISocketServer server, ILauncher launcher, Integer port, IConfiguration config, IMarshaller marshaller, ISendResponse responder) throws IOException
    {
        _server = server;
        _timeout = config.timeout();
        _port = port;
        _launcher = launcher;    
        _marshaller = marshaller;
        _factory = factory;
        _responder = responder;
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
                        hasMoreKeys = false;
                        continue;
                    }

                    if (key.isAcceptable())
                    {
                        ISocketChannel channel = _server.Accept(key);
                        long result = _launcher.launch(_factory.create(channel, _marshaller, _timeout, _launcher));
                    
                        if (result == -1)
                        {
                            HttpResponse response = new HttpResponse(503);
                            _responder.sendResponse(response, channel);
                            channel.close();
                        }
                    }
                    else
                    {
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