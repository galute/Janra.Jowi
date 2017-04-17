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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private final List<String> _values = new ArrayList<>();
    
    public Header(String key, String value)
    {
         _key = key;
         _values.addAll(parseValue(value));
    }
    
    public Header(IHeader header)
    {
        _key = header.key();
        addHeader(header);
    }
    
    public final void addHeader(IHeader header)
    {
        if (header == null || !_key.equals(header.key()))
        {
            return;
        }
        
        _values.addAll(((Header)header)._values);
    }
    
    @Override
    public String key()
    {
        return _key;
    }

    @Override
    public Integer occurences()
    {
        return _values.size();
    }
    
    @Override
    public String value()
    {
        return _values.get(0);
    }

    @Override
    public String value(Integer index)
    {
        if (index >=0 && index < _values.size())
        {
            return _values.get(index);
        }
        
        return null;
    }
    
    public String raw()
    {
        String raw = MessageFormat.format("{0}: ", _key);
        
        for (int i = 0; i < _values.size(); i++)
        {
            if (i > 0)
            {
                raw = raw + ", ";
            }
            raw = raw + _values.get(i);
        }
        
        return raw + "\r\n";
    }
    
    private List<String> parseValue(String value)
    {
        List<String>output = new ArrayList<>();
        
        String[] items = value.split(",", -1);
        
        Arrays.asList(items).forEach((item) ->{
            output.add(item.trim());
        });
        
        return output;
    }
}
