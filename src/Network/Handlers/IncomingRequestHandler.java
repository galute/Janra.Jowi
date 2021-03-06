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
import Pipeline.Configuration.Configuration;
import Protocol.Models.ResponseImpl;
import Protocol.Parsers.ProtocolException;
import Request.Processing.IMarshaller;
import Request.Processing.ISendResponse;
import Server.IConfiguration;
import Utilities.ILauncher;
import java.io.IOException;

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
    private final Configuration _config;
    
    
    public IncomingRequestHandler(IRequestHandlerFactory factory, ISocketServer server, ILauncher launcher, Integer port, IConfiguration config, IMarshaller marshaller, ISendResponse responder) throws IOException
    {
        _server = server;
        _timeout = config.timeout();
        _port = port;
        _launcher = launcher;    
        _marshaller = marshaller;
        _factory = factory;
        _responder = responder;
        _config = (Configuration)config;
    }
    
    @Override
    public void run()
    {
        try
        {
            _server.Configure(_port);

            while (!_stop)
            {
                try
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
                            long result = _launcher.launch(_factory.create(channel, _marshaller, _config, _launcher));

                            if (result == -1)
                            {
                                ResponseImpl response = new ResponseImpl(503);
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
                catch (IOException | ProtocolException ex)
                {
                    _config.handler().HandleException(ex);
                }
            }
        }
        catch (Exception ex)
        {
            _config.handler().HandleException(ex);
        }
        finally
        {
            try
            {
                _server.Close();
            } 
            catch (IOException ex)
            {
                _config.handler().HandleException(ex);
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