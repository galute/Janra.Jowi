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
import Protocol.Models.RequestBody;
import Protocol.Parsers.ProtocolException;
import Server.IHeader;
import java.io.IOException;

/**
 *
 * @author jmillen
 */
public class TransferEncodingProcessor
{
    private final EncodingReaders _readers;
    
    public TransferEncodingProcessor(EncodingReaders readers)
    {
        _readers = readers;
    }
    
    public RequestBody decode(ISocketChannel channel, IHeader encodingHeader, byte[] encodedData) throws ProtocolException, IOException
    {
        Integer numEncodings = encodingHeader.occurences();
        byte[] decodedData = encodedData;
        
        for (int i = numEncodings -1; i >= 0; i--)
        {
            decodedData = _readers.decode(encodingHeader.value(i), decodedData, channel);
        }
        
        return new RequestBody(decodedData);
    }
}
