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
package Tests.Stubs.Network.Readers;

import Network.Readers.IReader;
import Network.Wrappers.ISocketChannel;
import Protocol.Parsers.ProtocolException;
import java.io.IOException;

/**
 *
 * @author jmillen
 */
public class DeflateReaderStub implements IReader
{
    private final String _encoding;
    
    public DeflateReaderStub(String encoding)
    {
        _encoding = encoding;
    }
    
    @Override
    public String encoding()
    {
        return "deflate";
    }
    
    @Override
    public byte[] processData(byte[] data, ISocketChannel channel) throws ProtocolException, IOException
    {
        StringBuilder dataStr = new StringBuilder(new String(data, _encoding));
        dataStr.append(" ");
        dataStr.append(encoding());
        
        return dataStr.toString().getBytes(_encoding);
    }

    @Override
    public byte[] getBody(ISocketChannel channel) throws ProtocolException, IOException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
