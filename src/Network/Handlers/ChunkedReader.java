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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 *
 * @author jmillen
 */
public class ChunkedReader implements IReader
{
    private final Integer MaxRetries = 5;
    private final CharsetDecoder _decoder;
    
    public ChunkedReader()
    {
        _decoder = Charset.forName("ISO-8859-1").newDecoder();
    }
    
    @Override
    public RequestBody getBody(ISocketChannel channel) throws ProtocolException, IOException
    {
        Boolean moreChunks = true;
        byte[] buffer = new byte[2]; // always leave space for final \r\n
        int bufferLen = 0;
        
        while (moreChunks)
        {
            Integer size = readSize(channel);
            byte[] chunk = readChunk(channel, size);
            if (size > 0)
            {
                // increase buffer size to accommodate chunk
                byte[] newBuffer = new byte[buffer.length + chunk.length-2];
                System.arraycopy(buffer, 0, newBuffer, 0, bufferLen);
                buffer = newBuffer;
                // and append chunk to buffer
                System.arraycopy(chunk, 0, buffer, bufferLen, chunk.length-2);

                bufferLen += chunk.length - 2;
            }
            
            if (size == 0)
            {
                moreChunks = false;
                String ending = "\r\n";
                
                // use default Http encoding for now
                System.arraycopy(ending.getBytes("ISO-8859-1"), 0, buffer, bufferLen, 2);
            }
        }
        
        return new RequestBody(buffer);
    }
    
    private byte[] readChunk(ISocketChannel channel, Integer size) throws IOException, ProtocolException
    {
        Boolean readChunk = false;
        
        ByteBuffer bbuffer = ByteBuffer.allocate(size + 2);
        Integer readNothingCounter = 0;
        
        while (!readChunk)
        {
            int szRead = channel.read(bbuffer);
            
            if (szRead == 0)
            {
                readNothingCounter++;
            }
            else
            {
                readNothingCounter = 0;
            }
            
            if (readNothingCounter >= MaxRetries)
            {
                throw new IOException("Timeout after max retries of " + MaxRetries.toString());
            }
            
            if (szRead == -1 && bbuffer.hasRemaining())
            {
                throw new ProtocolException("Unable to read chunk, incomplete data", 400);
            }
            
            if (!bbuffer.hasRemaining())
            {
                readChunk = true;
            }
        }
        
        bbuffer.flip();
        
        return bbuffer.array();
    }
    
    private Integer readSize(ISocketChannel channel) throws IOException, ProtocolException
    {
        Boolean endOfLengthField = false;
        Boolean endStart = false;
        Boolean inExtensions = false;
        
        Integer readNothingCounter = 0;
        CharBuffer buffer = CharBuffer.allocate(2048);
        
        while (!endOfLengthField)
        {
            ByteBuffer bbuffer = ByteBuffer.allocate(1);

            int szRead = channel.read(bbuffer);
            
            if (szRead == 0)
            {
                readNothingCounter++;
            }
            else
            {
                readNothingCounter = 0;
            }
            
            if (szRead == -1)
            {
                throw new ProtocolException("Unable to read chunk, incomplete data", 400);
            }
            
            if (readNothingCounter >= MaxRetries)
            {
                throw new IOException("Timeout after max retries of " + MaxRetries.toString());
            }
            
            if (szRead > 0)
            {
                bbuffer.flip();


                CharBuffer buff = _decoder.decode(bbuffer);

                char value = buff.charAt(0);

                switch (value)
                {
                    case '\r':
                        endStart = true;
                        break;
                    case '\n':
                        if (endStart)
                        {
                            endOfLengthField = true;
                        }
                        else
                        {
                            throw new ProtocolException("Unexpected new line character", 400);
                        }
                        break;
                    case ';':
                        inExtensions = true;
                        break;
                    default:
                        endStart = false;
                        if (!inExtensions)
                        {
                            buffer = buffer.put(value);
                        }
                }
            }
        }
        buffer.flip();
        String lengthStr = new StringBuilder(buffer).toString();
        
        Integer length = -1;
        
        try
        {
            // rfc7230 4.1 does not specify a maximum chunk length
            // Assumption here that the chunk size will fit
            // into an integer. 
            length = Integer.parseInt(lengthStr, 16); 
        }
        catch (NumberFormatException ex)
        {
            throw new ProtocolException("Chunk size too big", 400);
        }
        
        return length;
    }
}
