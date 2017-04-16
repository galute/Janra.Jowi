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
package Tests.Protocol.Processing;

import Protocol.Models.HttpContext;
import Protocol.Models.HttpMethod;
import Protocol.Models.HttpRequest;
import Request.Processing.RequestProcessor;
import Tests.Stubs.Processing.MarshallerStub;
import Tests.Stubs.Processing.MarshallerStubNoPipelines;
import org.junit.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author jmillen
 */
public class RequestProcessorTests
{
    private RequestProcessor _unitUnderTest;
    private HttpContext _context;
    
    @Before
    public void setup()
    {
        _unitUnderTest = new RequestProcessor(new MarshallerStub());
        HttpRequest request = new HttpRequest(HttpMethod.GET, "my/path", "HTTP/1.1");
        _context = new HttpContext(request);
    }
    @Test
    public void returns404IfNoPipeline()
    {
        _unitUnderTest = new RequestProcessor(new MarshallerStubNoPipelines());
        
        HttpContext result = _unitUnderTest.processRequest(_context);
        
        try
        {
            String raw = result.response().getRaw();
            
            assertTrue(raw.contains("404"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
}