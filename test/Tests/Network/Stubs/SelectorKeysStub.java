/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests.Network.Stubs;

import Network.Wrappers.ISelectorKey;
import Network.Wrappers.ISelectorKeys;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jmillen
 */
public class SelectorKeysStub implements ISelectorKeys
{
    List<ISelectorKey> _keys = new ArrayList<>();
    
    public SelectorKeysStub(int numKeys, Boolean isAcceptable, Boolean isReadable)
    {
        for (int i = 0; i < numKeys; i++)
        {
            _keys.add(new SelectorKeyStub(isAcceptable, isReadable));
        }
    }
    
    public Integer NumKeys()
    {
        return _keys.size();
    }
    
    @Override
    public ISelectorKey GetNext()
    {
        if (_keys.isEmpty())
        {
            return null;
        }
        return _keys.remove(0);
    }
}
