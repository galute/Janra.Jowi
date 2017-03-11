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
