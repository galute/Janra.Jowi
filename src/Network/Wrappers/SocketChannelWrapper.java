/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network.Wrappers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 *
 * @author jmillen
 */
public class SocketChannelWrapper implements ISocketChannel
{
    SocketChannel _socket;
    Charset _charset=Charset.forName("ISO-8859-1");
    
    public SocketChannelWrapper(SocketChannel socket)
    {
        _socket = socket;
    }
    
    @Override
    public CharBuffer Read(Integer numBytes) throws IOException
    {
        Check();
        
        ByteBuffer buffer = ByteBuffer.allocate(numBytes);
        
        _socket.read(buffer);

        buffer.flip();
        
        CharsetDecoder decoder = _charset.newDecoder();
        return decoder.decode(buffer);
    }
    
    @Override
    public Integer Write(CharBuffer buffer) throws IOException
    {
        Check();
        
        CharsetEncoder encoder = _charset.newEncoder();
        Integer szWritten = _socket.write(encoder.encode(buffer));
        
        return szWritten;
    }
    
    @Override
    public void Close() throws IOException
    {
        if (isDisposed())
        {
            return;
        }
        _socket.close();
        _socket = null;
    }
    
    private void Check() throws IOException
    {
        if (isDisposed())
        {
            throw new IOException("Socket is disposed");
        }
    }
    
    private Boolean isDisposed()
    {
        return (_socket == null);
    }
}
