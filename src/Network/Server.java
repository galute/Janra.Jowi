/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network;

import Network.Wrappers.*;
import java.io.IOException;

/**
 *
 * @author jmillen
 */
public class Server
{
    IServerSocketChannel _serverSocket;
    ISelector _selector;
    
    public Server(IServerSocketChannel serverSocket, ISelector selector)
    {
        _serverSocket = serverSocket;
        _selector = selector;
    }
    
    public void Configure(Integer port) throws IOException
    {
        Check();
        _serverSocket.SetNonBlocking(Boolean.TRUE);
        _serverSocket.Bind(port);
        _selector.RegisterForAccepts(_serverSocket);
    }
    
    public ISelectorKeys Start() throws IOException
    {
        Check();
        return _selector.WaitForRequests();
    }
    
    public void Close() throws IOException
    {
        if (isDisposed())
        {
            return;
        }
        _selector.Close();
        _serverSocket.Close();
        _selector = null;
        _serverSocket = null;
    }
    
    private void Check() throws IOException
    {
        if (isDisposed())
        {
            throw new IOException("Socket is closed");
        }
    }
    
    private Boolean isDisposed()
    {
        return _serverSocket == null;
    }
}
