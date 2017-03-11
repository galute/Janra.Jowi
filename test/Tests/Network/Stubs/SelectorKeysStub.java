/*
 * Copyright (C) 2017 jmillen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
