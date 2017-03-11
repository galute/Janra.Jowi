/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests.Network.Stubs;

import Network.Wrappers.ISelectorKey;

/**
 *
 * @author jmillen
 */
public class SelectorKeyStub implements ISelectorKey
{
    public Boolean _isAcceptable = false;
    public Boolean _isReadable = false;
    
    public SelectorKeyStub(Boolean isAcceptable, Boolean isReadable)
    {
        _isAcceptable = isAcceptable;
        _isReadable = isReadable;
    }
    
    @Override
    public Boolean IsAcceptable()
    {
        return _isAcceptable;
    }

    @Override
    public Boolean IsReadable()
    {
        return _isReadable;
    }
    
}
