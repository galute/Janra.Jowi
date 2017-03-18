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
import Protocol.Parsers.IParser;
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
    IParser _parser;
    Integer _bufferSize;
    Charset _charset=Charset.forName("ISO-8859-1");
    
    public RequestHandler(ISelectorKey key, IParser parser, Integer bufferSize)
    {
        _key = key;
        _channel = null;
        _parser = parser;
        _bufferSize = bufferSize;
    }
    
    @Override
    public void run()
    {
        try
        {
            if (_key.isReadable())
            {
                _channel = _key.getChannel();
                
                String buffer = Read(_bufferSize);
                _parser.Parse(buffer);
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
    
    private String Read(Integer numBytes) throws IOException
    {
        int szRead = 1;
        
        ByteBuffer buffer = ByteBuffer.allocate(numBytes);
        
        while (szRead > 0 && buffer.remaining() > 0)
        {
            szRead = _channel.read(buffer);
        }

        buffer.flip();
        
        CharsetDecoder decoder = _charset.newDecoder();
        return new StringBuilder(decoder.decode(buffer)).toString();
    }
    
}
