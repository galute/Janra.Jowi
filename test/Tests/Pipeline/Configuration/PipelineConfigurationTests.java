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
package Tests.Pipeline.Configuration;

import Pipeline.Configuration.PipelineConfiguration;
import Server.IPipelineMiddleware;
import Tests.Protocol.Processing.*;
import Tests.Stubs.Processing.PipelineBuilderStub;
import java.util.Map;
import org.junit.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author jmillen
 */
public class PipelineConfigurationTests
{
    private PipelineConfiguration _unitUnderTest;
    private PipelineBuilderStub _builder;
    
    @Before
    public void setup()
    {
        _builder = new PipelineBuilderStub();
        _unitUnderTest = new PipelineConfiguration(_builder);
    }
    
    @Test
    public void CorrectlyOrdersMiddleware()
    {
        _unitUnderTest.addMiddleware("my/path", new MiddlewareStub());
        _unitUnderTest.addMiddleware("my/path", new MiddlewareStubTwo());
        _unitUnderTest.addMiddleware("my/path", new MiddlewareStubThree());
        
        _unitUnderTest.build();
        
        assertTrue(_builder.map.size() == 1);
        assertTrue(_builder.map.containsKey("my/path"));
        assertTrue(_builder.map.get("my/path").size() == 3);
        assertTrue(_builder.map.get("my/path").get(1) instanceof MiddlewareStub);
        assertTrue(_builder.map.get("my/path").get(2) instanceof MiddlewareStubTwo);
        assertTrue(_builder.map.get("my/path").get(3) instanceof MiddlewareStubThree);
    }
    
    @Test
    public void DoesNotMixRoutes()
    {
        _unitUnderTest.addMiddleware("my/path", new MiddlewareStub());
        _unitUnderTest.addMiddleware("my/path", new MiddlewareStubTwo());
        _unitUnderTest.addMiddleware("my/other/path", new MiddlewareStubThree());
        
        _unitUnderTest.build();
        assertTrue(_builder.map.size() == 2);
        assertTrue(_builder.map.containsKey("my/path"));
        assertTrue(_builder.map.containsKey("my/other/path"));
        assertTrue(_builder.map.get("my/path").size() == 2);
        assertTrue(_builder.map.get("my/other/path").size() == 1);
        assertTrue(_builder.map.get("my/path").get(1) instanceof MiddlewareStub);
        assertTrue(_builder.map.get("my/path").get(2) instanceof MiddlewareStubTwo);
        assertTrue(_builder.map.get("my/other/path").get(1) instanceof MiddlewareStubThree);
    }
    
    @Test
    public void MultipleMiddlewareOnDifferentRoutes()
    {
        _unitUnderTest.addMiddleware("my/path", new MiddlewareStub());
        _unitUnderTest.addMiddleware("my/path", new MiddlewareStubTwo());
        _unitUnderTest.addMiddleware("my/other/path", new MiddlewareStubThree());
        _unitUnderTest.addMiddleware("my/other/path", new MiddlewareStub());
        
        _unitUnderTest.build();
        assertTrue(_builder.map.size() == 2);
        assertTrue(_builder.map.containsKey("my/path"));
        assertTrue(_builder.map.containsKey("my/other/path"));
        assertTrue(_builder.map.get("my/path").size() == 2);
        assertTrue(_builder.map.get("my/other/path").size() == 2);
        assertTrue(_builder.map.get("my/path").get(1) instanceof MiddlewareStub);
        assertTrue(_builder.map.get("my/path").get(2) instanceof MiddlewareStubTwo);
        assertTrue(_builder.map.get("my/other/path").get(1) instanceof MiddlewareStubThree);
        assertTrue(_builder.map.get("my/other/path").get(2) instanceof MiddlewareStub);
        assertTrue(_builder.map.get("my/other/path").get(2) != _builder.map.get("my/path").get(1));
    }
    
    @Test
    public void ThrowsOnNullMiddleware()
    {
        try
        {
            _unitUnderTest.addMiddleware("my/path", null);
            _unitUnderTest.build();
            fail("Expected exception not thrown");
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof IllegalArgumentException);
            assertTrue("middleware and/or path invalid".equals(ex.getMessage()));
        }
    }
    
    @Test
    public void ThrowsOnEmptyPath()
    {
        try
        {
            _unitUnderTest.addMiddleware("", new MiddlewareStub());
            _unitUnderTest.build();
            fail("Expected exception not thrown");
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof IllegalArgumentException);
            assertTrue("middleware and/or path invalid".equals(ex.getMessage()));
        }
    }
    
    @Test
    public void ThrowsOnNullPath()
    {
        try
        {
            _unitUnderTest.addMiddleware(null, new MiddlewareStub());
            _unitUnderTest.build();
            fail("Expected exception not thrown");
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof IllegalArgumentException);
            assertTrue("middleware and/or path invalid".equals(ex.getMessage()));
        }
    }
}
