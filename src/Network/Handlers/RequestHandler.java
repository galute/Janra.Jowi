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
    private final ISelectorKey _key;
    IRequestBuilder _builder;
    IProcessRequest _processor;
    ISendResponse _responder;
    Charset _charset=Charset.forName("ISO-8859-1");
    
    
    public RequestHandler(ISelectorKey key, IRequestBuilder builder, IProcessRequest processor, ISendResponse responder)
    {
        _key = key;
        _builder = builder;
        _processor = processor;
        _responder = responder;
    }
    
    @Override
    public void run()
    {
        ISocketChannel channel;
        try
        {
            if (_key.isReadable())
            {
                channel = _key.getChannel();
                
                HttpContext context = _builder.ProcessRequest(channel);
                
                if (context.response().status() == 200)
                {
                    context = _processor.processRequest(context);
                }
                
                if (_key.isWriteable())
                {
                    _responder.sendResponse(context.response(), channel);
                }
                else
                {
                    throw new IOException("Failed to send response");
                }
            }
            else
            {
                _key.cancel();
            }
        }
        catch (Exception ex)
        {
            // To-do (try to) send 500
        }
    }
}
