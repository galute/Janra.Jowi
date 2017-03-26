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
import Protocol.Parsers.IParser;
import Protocol.Builders.IRequestBuilder;
import Request.Processing.IProcessRequest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 *
 * @author jmillen
 */
public class RequestHandler implements Runnable
{
    private final ISelectorKey _key;
    ISocketChannel _channel;
    IRequestBuilder _builder;
    IProcessRequest _processor;
    Charset _charset=Charset.forName("ISO-8859-1");
    
    public RequestHandler(ISelectorKey key, IRequestBuilder builder, IProcessRequest processor)
    {
        _key = key;
        _channel = null;
        _builder = builder;
        _processor = processor;
    }
    
    @Override
    public void run()
    {
        try
        {
            if (_key.isReadable())
            {
                _channel = _key.getChannel();
                
                HttpContext context = _builder.ProcessRequest(_channel);
                
                if (context.response().status() == 200)
                {
                    _processor.processRequest(context);
                }
                else
                {
                    // To-do send back response
                }
            }
            else
            {
                _key.cancel();
            }
        }
        catch (Exception ex)
        {
        }
    }
}
