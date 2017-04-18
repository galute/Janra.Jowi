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
import Protocol.Models.Headers;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class HeadersTests
{
    @Test
    public void CopyConstructorDoesNotKeepReferences()
    {
        Headers first = new Headers();
        first.addHeader(Header.create("first key", "first value"));
        Headers second = new Headers(first);
        
        first.addHeader(Header.create("second key", "second value"));
        assertTrue(second.get("second key") == null);
    }
    
    // rfs7320 Section 3.2 Header name is case-insensitive
    @Test
    public void HeaderNameIsCaseInsensitive()
    {
        Headers headers = new Headers();
        headers.addHeader(Header.create("FiRsT", "value"));
        
        assertTrue("value".equals(headers.get("first").value(0)));
    }
    
    @Test
    public void DuplicateHeaderIsAccessible()
    {
        Headers headers = new Headers();
        headers.addHeader(Header.create("First", "value"));
        headers.addHeader(Header.create("First", "value2"));
        
        assertTrue(headers.get("First").occurences() == 2);
        assertTrue("value".equals(headers.get("First").value()));
        assertTrue("value".equals(headers.get("First").value(0)));
        assertTrue("value2".equals(headers.get("First").value(1)));
    }
    
    @Test
    public void CommaSeparatedHeaderValueIsAccessible()
    {
        Headers headers = new Headers();
        headers.addHeader(Header.create("First", "value, value2, value3"));
        
        assertTrue(headers.get("First").occurences() == 3);
        assertTrue("value".equals(headers.get("First").value()));
        assertTrue("value".equals(headers.get("First").value(0)));
        assertTrue("value2".equals(headers.get("First").value(1)));
        assertTrue("value3".equals(headers.get("First").value(2)));
    }
    
    @Test
    public void OnlyOneContentHeaderType()
    {
        Headers headers = new Headers();
        headers.addHeader(Header.create("Content-type", "value"));
        headers.addHeader(Header.create("Content-type", "value2"));
        
        assertTrue(headers.get("Content-type").occurences() == 1);
        assertTrue("value2".equals(headers.get("Content-type").value()));
    }
}
