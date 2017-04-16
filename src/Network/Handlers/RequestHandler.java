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

import Network.Wrappers.*;
import Protocol.Models.*;
import Protocol.Builders.IRequestBuilder;
import Request.Processing.IProcessRequest;
import Request.Processing.ISendResponse;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 * @author jmillen
 */
public class RequestHandler implements Runnable
{
    private final ISelector _selector;
    private final long _timeout;
    IRequestBuilder _builder;
    IProcessRequest _processor;
    ISendResponse _responder;
    Charset _charset=Charset.forName("ISO-8859-1");
    
    
    public RequestHandler(ISelector selector, ISocketChannel channel, IRequestBuilder builder, IProcessRequest processor, ISendResponse responder, long timeout) throws IOException
    {
        _selector = selector;
        _builder = builder;
        _processor = processor;
        _responder = responder;
        _timeout = timeout;
        
        selector.registerForReads(channel);
    }
    
    @Override
    public void run()
    {
        Boolean isFinished = false;
        ISocketChannel channel = null;
        
        try
        { 
            while (!isFinished)
            {
                ISelectorKeys keys = _selector.waitForRequests(_timeout);

                if (keys == null)
                {
                    isFinished = true;
                    continue;
                }

                ISelectorKey key = keys.getNext();

                if (key.isReadable())
                {
                    channel = key.getChannel();
                    HttpContext context = _builder.ProcessRequest(channel);

                    if (context.response().status() == 200)
                    {
                        context = _processor.processRequest(context);
                    }

                    _responder.sendResponse(context.response(), channel);
                    isFinished = true;
                }
                else
                {
                    isFinished = true;
                    key.getChannel().close();
                    key.cancel();
                }
            }

            if (channel != null)
            {
                channel.close();
            }
        }
        catch (Exception ex)
        {
            // To-do (try to) send 500
        }
    }
}
