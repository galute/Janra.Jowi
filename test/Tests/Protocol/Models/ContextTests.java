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
import Protocol.Models.HttpContext;
import Protocol.Models.HttpMethod;
import Protocol.Models.HttpRequest;
import Protocol.Models.ResponseImpl;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class ContextTests
{
    @Test
    public void NoBodyIfButWithHeadersForHeadRequest()
    {
        try
        {
            HttpRequest request = new HttpRequest(HttpMethod.HEAD, "blaa", "HTTP/1.1");
            HttpContext context = new HttpContext(request);

            context.response().setBody("Test body");

            ResponseImpl response = (ResponseImpl)context.response();
            String result = response.getRaw();
            assertTrue(result.contains("Content-type"));
            assertFalse(result.contains("Test body"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void NoBodyOrHeadersForConnectRequest()
    {
        try
        {
            HttpRequest request = new HttpRequest(HttpMethod.CONNECT, "blaa", "HTTP/1.1");
            HttpContext context = new HttpContext(request);

            context.response().setBody("Test body");

            ResponseImpl response = (ResponseImpl)context.response();
            String result = response.getRaw();
            assertFalse(result.contains("Content-type"));
            assertFalse(result.contains("Test body"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
}
