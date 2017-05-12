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

import Protocol.Models.ContentType;
import Protocol.Models.Header;
import Protocol.Parsers.ProtocolException;
import Server.IHeader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class ContentTypeTests
{
    @Test
    public void ExtractMediaTypeAndCharset()
    {
        try
        {
            IHeader header = new Header("content-type", "text/plain;charset=UTF-8");
            ContentType contentType = new ContentType(header, "foobar");

            assertEquals("text/plain", contentType.mediaType());
            assertEquals("UTF-8", contentType.charset());
        }
        catch (ProtocolException ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ExtractMediaTypeOnly()
    {
        try
        {
            IHeader header = new Header("content-type", "text/plain");
            ContentType contentType = new ContentType(header, "foobar");

            assertEquals("text/plain", contentType.mediaType());
            assertEquals("foobar", contentType.charset());
        }
        catch (ProtocolException ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void AcceptsQuotedCharset()
    {
        try
        {
            IHeader header = new Header("content-type", "text/plain;charset=\"UTF-8\"");
            ContentType contentType = new ContentType(header, "foobar");

            assertEquals("text/plain", contentType.mediaType());
            assertEquals("UTF-8", contentType.charset());
        }
        catch (ProtocolException ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void RejectsInvalidCharsetsyntax()
    {
        try
        {
            IHeader header = new Header("content-type", "text/plain;charset UTF-8");
            ContentType contentType = new ContentType(header, "foobar");

            fail("Expected Exception not thrown");
        }
        catch (ProtocolException ex)
        {
            assertEquals("Unrecognised charset format", ex.getMessage());
        }
    }
    
    @Test
    public void CharsetNameIsCaseInsensitive()
    {
        try
        {
            IHeader header = new Header("content-type", "text/plain;Charset=UTF-8");
            ContentType contentType = new ContentType(header, "foobar");

            assertEquals("text/plain", contentType.mediaType());
            assertEquals("UTF-8", contentType.charset());
        }
        catch (ProtocolException ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void DoesNotAllowWhiteSpaceAfterCharset()
    {
        try
        {
            IHeader header = new Header("content-type", "text/plain;Charset =UTF-8");
            ContentType contentType = new ContentType(header, "foobar");

            fail("Expected Exception not thrown");
        }
        catch (ProtocolException ex)
        {
            assertEquals("Unrecognised charset format", ex.getMessage());
        }
    }
    
    @Test
    public void DoesNotAllowWhiteSpaceAfterEquals()
    {
        try
        {
            IHeader header = new Header("content-type", "text/plain;Charset= UTF-8");
            ContentType contentType = new ContentType(header, "foobar");

            fail("Expected Exception not thrown");
        }
        catch (ProtocolException ex)
        {
            assertEquals("Unrecognised charset format", ex.getMessage());
        }
    }
    
    @Test
    public void MediadTypeAndSubtypeAreCaseInsensitive()
    {
        // rfc7231 Sect 3.1.1.1 The type, subtype, and parameter name tokens are case-insensitive.
        try
        {
            IHeader header = new Header("content-type", "Text/Plain;charset=UTF-8");
            ContentType contentType = new ContentType(header, "foobar");

            assertEquals("text/plain", contentType.mediaType());
            assertEquals("UTF-8", contentType.charset());
        }
        catch (ProtocolException ex)
        {
            fail("Unexpected Exception thrown: " + ex.getMessage());
        }
    }
}
