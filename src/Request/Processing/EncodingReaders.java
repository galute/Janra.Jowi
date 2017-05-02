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

import Network.Readers.IReader;
import Network.Wrappers.ISocketChannel;
import Protocol.Parsers.ProtocolException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jmillen
 */
public class EncodingReaders
{
    private final Map<String, IReader>_readers = new HashMap<>();
    
    public void addReader(IReader reader)
    {
        if (reader == null)
        {
            throw new IllegalArgumentException("Reader cannot be null");
        }
        _readers.put(reader.encoding(), reader);
    }
    
    public byte[] decode(String encoding, byte[] data, ISocketChannel channel) throws ProtocolException, IOException
    {
        if (!_readers.containsKey(encoding))
        {
            throw new ProtocolException(encoding + " transfer encoding not supported.", 501);
        }
        
        return _readers.get(encoding).processData(data, channel);
    }
}
