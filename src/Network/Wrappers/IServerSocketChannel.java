/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network.Wrappers;

import java.io.IOException;

/**
 *
 * @author jmillen
 */
public interface IServerSocketChannel
{
    void SetNonBlocking(Boolean flag) throws IOException;
    void Bind(Integer port) throws IOException;
    void Close() throws IOException;
}
