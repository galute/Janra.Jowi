/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network.Wrappers;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

/**
 *
 * @author jmillen
 */
public interface ISelector
{
    public void RegisterForAccepts(IServerSocketChannel serverChannel) throws ClosedChannelException, IOException;
    public void RegisterForReads(ISocketChannel serverChannel) throws ClosedChannelException, IOException;
    public ISelectorKeys WaitForRequests() throws IOException;
    public void Close()  throws IOException;
}
