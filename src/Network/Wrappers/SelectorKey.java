/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network.Wrappers;

import java.nio.channels.SelectionKey;

/**
 *
 * @author jmillen
 */
public class SelectorKey implements ISelectorKey
{
    private final SelectionKey _key;
    
    public SelectorKey(SelectionKey key)
    {
        _key = key;
    }
    
    @Override
    public Boolean IsAcceptable()
    {
        return _key.isAcceptable();
    }

    @Override
    public Boolean IsReadable()
    {
        return _key.isReadable();
    }
}
