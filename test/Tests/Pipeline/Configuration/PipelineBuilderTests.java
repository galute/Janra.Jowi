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

import Tests.Stubs.Middleware.MiddlewareStubTwo;
import Tests.Stubs.Middleware.MiddlewareStubThree;
import Tests.Stubs.Middleware.MiddlewareStub;
import Pipeline.Configuration.*;
import Pipeline.IPipeline;
import Protocol.Models.HttpContext;
import Request.Processing.RequestContext;
import Server.IPipelineMiddleware;
import Tests.Protocol.Processing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class PipelineBuilderTests
{
    IPipelineBuilder _unitUnderTest;
    private RequestContext _context;
    Map<String, Map<Integer, IPipelineMiddleware>> _config = new HashMap<>();
    Map<Integer, IPipelineMiddleware>_pipeline;
    
    @Before
    public void setup()
    {
        HttpContext httpContext = new HttpContext(200);
        
        _context = new RequestContext(httpContext);
        _unitUnderTest = new PipelineBuilder();
        
        _config.clear();
        _pipeline = new TreeMap(Collections.reverseOrder());
    }
    
    @Test
    public void SimpleSingleMiddlewarePipeline()
    {
        _pipeline.put(1, new MiddlewareStub());
        _config.put("my/path", _pipeline);
        
        List<IPipeline>result = _unitUnderTest.build(_config);
        
        assertTrue(result.size() == 1);
        assertTrue(((IPipeline)result.get(0)).isPipeline("my/path"));
        
       ((IPipeline)result.get(0)).run(_context);
       
       assertTrue(_context.Properties().Property("Module") instanceof MiddlewareStub);
    }
    
    @Test
    public void singlePipelineMultipleMiddleware()
    {
        _pipeline.put(1, new MiddlewareStub());
        _pipeline.put(2, new MiddlewareStubTwo());
        _pipeline.put(3, new MiddlewareStubThree());
        _config.put("my/path", _pipeline);
        
        List<IPipeline>result = _unitUnderTest.build(_config);
        
        assertTrue(result.size() == 1);
        assertTrue(((IPipeline)result.get(0)).isPipeline("my/path"));
        
       ((IPipeline)result.get(0)).run(_context);
       
       assertTrue(_context.Properties().Property("Module") instanceof MiddlewareStub);
       assertTrue(_context.Properties().Property("Module2") instanceof MiddlewareStubTwo);
       assertTrue(_context.Properties().Property("Module3") instanceof MiddlewareStubThree);
    }
    
    @Test
    public void multiplePipelinesMultipleMiddleware()
    {
        _pipeline.put(1, new MiddlewareStub());
        _pipeline.put(2, new MiddlewareStubTwo());
        _pipeline.put(3, new MiddlewareStubThree());
        _config.put("my/path", _pipeline);
        
        _pipeline = new TreeMap(Collections.reverseOrder());
        
        _pipeline.put(1, new MiddlewareStub());
        _pipeline.put(3, new MiddlewareStubThree());
        _config.put("my/other/path", _pipeline);
        
        List<IPipeline>result = _unitUnderTest.build(_config);
        
        assertTrue(result.size() == 2);
        assertTrue(((IPipeline)result.get(0)).isPipeline("my/path"));
        assertTrue(((IPipeline)result.get(1)).isPipeline("my/other/path"));
        
       ((IPipeline)result.get(1)).run(_context);
       
       assertTrue(_context.Properties().Property("Module") instanceof MiddlewareStub);
       assertTrue(_context.Properties().Property("Module2") == null);
       assertTrue(_context.Properties().Property("Module3") == null);
       assertTrue(_context.Properties().Property("Two") == null);
       assertTrue(_context.Properties().Property("Three") instanceof MiddlewareStubThree);
    }
}
