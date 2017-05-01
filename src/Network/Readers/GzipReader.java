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
import Protocol.Models.RequestBody;
import Protocol.Parsers.ProtocolException;
import Server.IHeader;
import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 *
 * @author jmillen
 */
public class GzipReader implements IReader
{
    private Integer _length;
    private final ChannelReader _reader;
    
    public GzipReader()
    {
        _length = -1;
        _reader = null;
    }
    
    public GzipReader(IHeader contentLength) throws ProtocolException
    {
        try
        {
            _length = Integer.parseInt(contentLength.value());
        }
        catch (NumberFormatException ex)
        {
            throw new ProtocolException("Invalid content-length", 400);
        }
        _reader = new ChannelReader();
    }
    
    @Override
    public byte[] processData(byte[] data) throws ProtocolException, IOException
    {
        Integer result = 0;
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
        ByteArrayOutputStream  bytesOut;
        try (GZIPInputStream gzippedIn = new GZIPInputStream(bytesIn))
        {
            bytesOut = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (result >= 0)
            {
                result = gzippedIn.read(buffer, 0, buffer.length);
                
                if (result > 0)
                {
                    bytesOut.write(buffer, 0, result);
                }
            }
        }
        bytesOut.close();
        
        return bytesOut.toByteArray();
    }

    @Override
    public RequestBody getBody(ISocketChannel channel) throws ProtocolException, IOException
    {
        if (_reader == null)
        {
            throw new IllegalArgumentException("GzipReader wrong constructor used");
        }
        byte[] zippedData = _reader.readBytes(channel, _length + 2);
        byte[] unzippedData = processData(zippedData);
        return new RequestBody(unzippedData);
    }
}
