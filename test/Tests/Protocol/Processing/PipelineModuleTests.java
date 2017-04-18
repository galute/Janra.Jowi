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

import Tests.Stubs.Middleware.MiddlewareStubTwo;
import Tests.Stubs.Middleware.MiddlewareStubThree;
import Tests.Stubs.Middleware.MiddlewareStub;
import Protocol.Models.HttpContext;
import Request.Processing.PipelineModule;
import Request.Processing.RequestContext;
import Server.IPipelineMiddleware;
import org.junit.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author jmillen
 */
public class PipelineModuleTests
{
    private PipelineModule _unitUnderTest;
    private RequestContext _context;
    IPipelineMiddleware _middleware;
    
    @Before
    public void setup()
    {
        _middleware = new MiddlewareStub();
        _unitUnderTest = new PipelineModule(_middleware);
        HttpContext httpContext = new HttpContext(200);
        
        _context = new RequestContext(httpContext);
    }
    
    @Test
    public void InvokesSingleMiddleware()
    {
        try
        {
            _unitUnderTest.Invoke(_context);
        
            String result = _context.getResponse().getRaw();
            assertTrue(result.contains("MiddlewareStub Body"));
            assertTrue(result.contains("Content-type: application/xml"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
        
    }
    
    @Test
    public void InvokesMiddlewareStack()
    {
        try
        {
            IPipelineMiddleware middleware2 = new MiddlewareStubTwo();
            PipelineModule module = new PipelineModule(middleware2);
            _unitUnderTest = new PipelineModule(_middleware, module);
            _unitUnderTest.Invoke(_context);
            String result = _context.getResponse().getRaw();
            assertFalse(result.contains("MiddlewareStub Body"));
            assertFalse(result.contains("Content-type: application/xml"));
            assertTrue(result.contains("MiddlewareStubTwo Body"));
            assertTrue(result.contains("Content-type: application/json"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void DoesNotInvokeMiddlewareStackOnReturnFalse()
    {
        try
        {
            IPipelineMiddleware middleware2 = new MiddlewareStubThree();
            PipelineModule module = new PipelineModule(middleware2);
            _middleware = new MiddlewareStubTwo();
            _unitUnderTest = new PipelineModule(_middleware, module);
            _unitUnderTest.Invoke(_context);
            String result = _context.getResponse().getRaw();
            assertTrue(result.contains("MiddlewareStubThree Body"));
            assertTrue(result.contains("Content-type: application/xml"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
}
