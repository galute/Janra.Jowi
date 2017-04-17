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
package Tests.Protocol.Models;

import Protocol.Models.Header;
import org.junit.*;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author jmillen
 */
public class HeaderTests
{
    private Header _unitUnderTest;
    
    @Before
    public void setup()
    {
        _unitUnderTest = new Header("key", "value");
    }
    
    @Test
    public void rawReturnsSingleValue()
    {
        String result = _unitUnderTest.raw();
        assertTrue("key: value\r\n".equals(result));
    }
    
    @Test
    public void rawReturnsCommaSeparatedList()
    {
        _unitUnderTest = new Header("key", "value, value2, value3");
        String result = _unitUnderTest.raw();
        assertTrue("key: value, value2, value3\r\n".equals(result));
    }
    
    @Test
    public void singleValueCorrectlyParsed()
    {
        assertTrue(_unitUnderTest.occurences() == 1);
        assertTrue("value".equals(_unitUnderTest.value()));
        assertTrue("value".equals(_unitUnderTest.value(0)));
        assertTrue(_unitUnderTest.value(1) == null);
    }
    
    @Test
    public void CommaSeparatedvalueCorrectlyParsed()
    {
        _unitUnderTest = new Header("key", "value, value2, value3");
        assertTrue(_unitUnderTest.occurences() == 3);
        assertTrue("value".equals(_unitUnderTest.value()));
        assertTrue("value".equals(_unitUnderTest.value(0)));
        assertTrue("value2".equals(_unitUnderTest.value(1)));
        assertTrue("value3".equals(_unitUnderTest.value(2)));
    }
}
