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
import Server.IHeader;
import java.io.IOException;

/**
 *
 * @author jmillen
 */
public class IdentityReader implements IReader
{
    private Integer _length;
    private final ChannelReader _reader;
    
    public IdentityReader()
    {
        _length = -1;
        _reader = null;
    }
    
    public IdentityReader(IHeader contentLength) throws ProtocolException
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
    public String encoding()
    {
        return "identity";
    }
    
    @Override
    public byte[] getBody(ISocketChannel channel) throws ProtocolException, IOException
    {
        if (_reader == null)
        {
            throw new IllegalArgumentException("IdentityReader wrong constructor");
        }
        byte[] data = _reader.readBytes(channel, _length);
        
        return data;
    }

    @Override
    public byte[] processData(byte[] data, ISocketChannel channel) throws ProtocolException, IOException
    {
        return _reader.readBytes(channel, _length);
    }
}
