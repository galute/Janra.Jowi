/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import Network.Wrappers.ISocketChannel;
import java.io.IOException;
import java.nio.CharBuffer;

/**
 *
 * @author jmillen
 */
public class SocketStub implements ISocketChannel
{
    CharBuffer _readBuffer = null;
    CharBuffer _writeBuffer = null;
    Boolean isClosed = false;
    
    public void SetReadBuffer(CharBuffer buffer)
    {
        _readBuffer = buffer;
    }
    
    public CharBuffer GetWriteBuffer()
    {
        return _writeBuffer;
    }
    @Override
    public CharBuffer Read(Integer numBytes) throws IOException
    {
        return _readBuffer;
    }

    @Override
    public Integer Write(CharBuffer buffer) throws IOException
    {
        _writeBuffer = buffer;
        return buffer.length();
    }

    @Override
    public void Close() throws IOException
    {
        isClosed = true;
    }
    
}
