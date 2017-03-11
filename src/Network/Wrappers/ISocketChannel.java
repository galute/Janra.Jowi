/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network.Wrappers;

import java.io.IOException;
import java.nio.CharBuffer;

/**
 *
 * @author jmillen
 */
public interface ISocketChannel
{
    public CharBuffer Read(Integer numBytes) throws IOException;
    public Integer Write(CharBuffer buffer) throws IOException;
    public void Close() throws IOException;
}
