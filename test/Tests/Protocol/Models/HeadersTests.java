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
        first.addHeader(new Header("first key", "first value"));
        Headers second = new Headers(first);
        
        first.addHeader(new Header("second key", "second value"));
        assertTrue(second.get("second key") == null);
    }
}
