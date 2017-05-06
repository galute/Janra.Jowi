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
package Network.Readers;

import Network.Wrappers.ISocketChannel;
import Protocol.Parsers.ProtocolException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 *
 * @author jmillen
 */
public class ChannelReader
{
    private final Integer MaxRetries = 5;
    
    private final CharsetDecoder _decoder;
    
//    public ChannelReader()
//    {
//        _decoder = Charset.forName("ISO-8859-1").newDecoder();
//    }
    
    public ChannelReader(String charset)
    {
        _decoder = Charset.forName(charset).newDecoder();
    }
    
    public byte[] readLine(ISocketChannel channel) throws IOException, ProtocolException
    {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        Boolean endOfLine = false;
        Boolean endStart = false;
        
        while (!endOfLine)
        {
            ByteBuffer read = readBytesPrivate(channel, 1);
            
            CharBuffer buff = _decoder.decode(read);

            char value = buff.charAt(0);

            switch (value)
            {
                case '\r':
                    endStart = true;
                    break;
                case '\n':
                    if (endStart)
                    {
                        endOfLine = true;
                    }
                    else
                    {
                        throw new ProtocolException("Unexpected new line character", 400);
                    }
                    break;
                default:
                    endStart = false;
                    bytesOut.write(read.array());
            }
        }
        
        return bytesOut.toByteArray();
    }
    
    public byte[] readBytes(ISocketChannel channel, Integer numBytes) throws IOException, ProtocolException
    {
        // To-do if length changed to double, test here for length > MAX_VALUE and if
        // it is, do multiple calls. Fewer problems with incompatibility doing it
        // that way compared to passing double to private readBytes
        return readBytesPrivate(channel, numBytes).array();
    }
    
    private ByteBuffer readBytesPrivate(ISocketChannel channel, Integer numBytes) throws IOException, ProtocolException
    {
        Boolean readAllBytes = false;
        Integer readNothingCounter = 0;
        ByteBuffer bbuffer = ByteBuffer.allocate(numBytes);
        
        while (!readAllBytes)
        {
            int szRead = channel.read(bbuffer);
            
            if (szRead == 0)
            {
                readNothingCounter++;
            }
            
            if (szRead == -1)
            {
                throw new ProtocolException("Unexpected end of data", 400);
            }
            
            if (readNothingCounter >= MaxRetries)
            {
                throw new IOException("Timeout after max retries of " + MaxRetries.toString());
            }
            
            if (!bbuffer.hasRemaining())
            {
                readAllBytes = true;
            }
        }
        
        bbuffer.flip();
        return bbuffer;
    }
}
