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
package Protocol.Models;

import Server.IHeader;

/**
 *
 * @author jmillen
 */
public class Header implements IHeader
{
    public static IHeader create(String key, String value)
    {
        return new Header(key, value);
    }
    
    private final String _key;
    private final String _value;
    
    private Header(String key, String value)
    {
         _key = key;
         _value = value;
    }
    
    @Override
    public String key()
    {
        return _key;
    }

    @Override
    public Integer occurences()
    {
        return 1;
    }

    @Override
    public String value(Integer index)
    {
        if (index == 0)
        {
            return _value;
        }
        
        return null;
    }
}
