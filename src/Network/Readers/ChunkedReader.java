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
import java.io.IOException;

/**
 *
 * @author jmillen
 */
public class ChunkedReader implements IReader
{
    private final ChannelReader _reader;
    private final String _encoding;
    
    public ChunkedReader()
    {
        _encoding = "ISO-8859-1";
        _reader = new ChannelReader(_encoding);
    }
    
    @Override
    public String encoding()
    {
        return "chunked";
    }
    
    @Override
    public byte[] getBody(ISocketChannel channel) throws ProtocolException, IOException
    {
        Boolean moreChunks = true;
        byte[] buffer = new byte[0];
        int bufferLen = 0;
        
        while (moreChunks)
        {
            Integer size = readSize(channel);
            byte[] chunk = _reader.readBytes(channel, size + 2);//readChunk(channel, size);
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
            else
            {
                moreChunks = false;
            }
        }
        
        return buffer;
    }
    
    private Integer readSize(ISocketChannel channel) throws IOException, ProtocolException
    {
        byte[] line = _reader.readLine(channel);
        
        String[] fields = new String(line, _encoding).split(";");
        
        Integer length = -1;
        
        try
        {
            // rfc7230 4.1 does not specify a maximum chunk length
            // Assumption here that the chunk size will fit
            // into an integer. 
            length = Integer.parseInt(fields[0], 16); 
        }
        catch (NumberFormatException ex)
        {
            throw new ProtocolException("Chunk size too big or invalid", 400);
        }
        
        return length;
    }

    @Override
    public byte[] processData(byte[] data, ISocketChannel channel) throws ProtocolException, IOException
    {
        return getBody(channel);
    }
}
