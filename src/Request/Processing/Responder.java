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
package Request.Processing;

import Network.Wrappers.ISocketChannel;
import Protocol.Builders.IResponseBuilder;
import Protocol.Models.ResponseImpl;
import Protocol.Parsers.ProtocolException;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;

/**
 *
 * @author jmillen
 */
public class Responder implements ISendResponse
{
    private final IResponseBuilder _builder;
    
    public Responder(IResponseBuilder builder)
    {
        _builder = builder;
    }
    
    @Override
    public void sendResponse(ResponseImpl response, ISocketChannel channel) throws ProtocolException, CharacterCodingException, IOException
    {
        ByteBuffer buffer = _builder.BuildResponse(response);
        Integer retries = 0;
        
        while (buffer.hasRemaining() && retries < 5)
        {
            channel.write(buffer);
            try
            {
                if (buffer.hasRemaining())
                {
                    retries++;
                    Thread.sleep(100);
                    // To-do use scheduler here instead of sleep
                }
            }
            catch (Exception ex)
            {
                // do nothing
            }
        }
    }  
}
