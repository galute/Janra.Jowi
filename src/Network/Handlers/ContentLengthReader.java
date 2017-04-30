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

import Network.Wrappers.ISocketChannel;
import Protocol.Models.RequestBody;
import Protocol.Parsers.ProtocolException;
import Server.IHeader;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author jmillen
 */
public class ContentLengthReader implements IReader
{
    private final Integer MaxRetries = 5;
    private Integer _length;
    
    public ContentLengthReader(IHeader contentLength) throws ProtocolException
    {
        try
        {
            _length = Integer.parseInt(contentLength.value());
        }
        catch (NumberFormatException ex)
        {
            throw new ProtocolException("Invalid content-length", 400);
        }
    }
    
    @Override
    public RequestBody getBody(ISocketChannel channel) throws ProtocolException, IOException
    {
        Boolean finished = false;
        Integer readNothingCounter = 0;

        // extra 2 to account for \r\n
        ByteBuffer bbuffer = ByteBuffer.allocate(_length + 2);
            
        while (!finished)
        {
            Integer sizeRead = channel.read(bbuffer);

            if (!bbuffer.hasRemaining())
            {
                // don't test for sizeRead = -1 here
                // as we have all the data currently needed
                finished = true;
            }
            
            if (sizeRead == 0)
            {
                readNothingCounter++;
            }
            
            if (readNothingCounter >= MaxRetries ||
                (sizeRead == -1 && bbuffer.hasRemaining()))
            {
                throw new IOException("Incomplete data read");
            }
        }
        if (!bbuffer.hasArray())
        {
            throw new ProtocolException("Failed to read body", 500);
        }
        return new RequestBody(bbuffer.array());
    }
}
