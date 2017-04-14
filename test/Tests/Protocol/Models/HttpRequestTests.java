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
import Protocol.Models.HttpMethod;
import Protocol.Models.HttpRequest;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class HttpRequestTests
{
    @Test
    public void CopyConstructorDoesNotKeepReferences()
    {
        Headers firstHeaders = new Headers();
        Headers secondHeaders = new Headers();
        firstHeaders.addHeader(new Header("First Header", "First Header Value"));
        secondHeaders.addHeader(new Header("Second Header", "Second Header Value"));
        
        HttpRequest first = new HttpRequest(HttpMethod.POST, "first path","first version");
        first.addHeaders(firstHeaders);
        
        HttpRequest second = new HttpRequest(first);
        second.addHeaders(secondHeaders);
        
        assertTrue(first.header("Second Header") == null);
        
    }
}
