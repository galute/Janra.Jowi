/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests.Network.Stubs;

import Network.Wrappers.IServerSocketChannel;
import java.io.IOException;

/**
 *
 * @author jmillen
 */
public class ServerSocketStub implements IServerSocketChannel
{
    public Boolean _nonBlockingFlag = null;
    public Boolean _isClosed = false;
    public Integer _boundPort = null;
    
    @Override
    public void SetNonBlocking(Boolean flag) throws IOException
    {
        _nonBlockingFlag = flag;
    }

    @Override
    public void Bind(Integer port) throws IOException
    {
        _boundPort = port;
    }

    @Override
    public void Close() throws IOException
    {
        _isClosed = true;
    }
}
