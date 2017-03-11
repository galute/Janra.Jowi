/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network.Wrappers;

import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author jmillen
 */
public class SelectorKeys implements ISelectorKeys
{
    Iterator _iter;
    
    public SelectorKeys(Set keys)
    {
        _iter = keys.iterator();
    }
    
    @Override
    public ISelectorKey GetNext()
    {
        if (_iter.hasNext())
        {
            SelectionKey key = (SelectionKey)_iter.next();
            _iter.remove();
            return new SelectorKey(key);
        }
        
        return null;
    }
}
