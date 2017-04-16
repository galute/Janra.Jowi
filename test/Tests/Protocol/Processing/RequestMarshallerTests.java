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

import Pipeline.Configuration.InvalidConfigurationException;
import Request.Processing.Pipeline;
import Request.Processing.PipelineModule;
import Request.Processing.RequestMarshaller;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author jmillen
 */
public class RequestMarshallerTests
{
    private RequestMarshaller _unitUnderTest;
    private final List<Pipeline> _pipelines = new ArrayList<>();
    private static PipelineModule _module;
    
    @BeforeClass
    public static void oneTimeSetup()
    {
        _module = new PipelineModule(new MiddlewareStub());
    }
    
    @Before
    public void setup()
    {
        _pipelines.clear();
    }
    
    @Test
    public void ThowsExceptionForDuplicatePaths()
    {
        Pipeline pipeline1 = new Pipeline("my/path", _module);
        Pipeline pipeline2 = new Pipeline("my/path", _module);
        _pipelines.add(pipeline1);
        _pipelines.add(pipeline2);
        
        _unitUnderTest = new RequestMarshaller(_pipelines);
        
        try
        {
            _unitUnderTest.pipeline("my/path");
            fail("Expected exception not thrown");
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof InvalidConfigurationException);
        }
    }
    
    @Test
    public void SelectsCorrectPipelineForPath()
    {
        Pipeline pipeline1 = new Pipeline("my/path", _module);
        Pipeline pipeline2 = new Pipeline("my/other/path", _module);
        _pipelines.add(pipeline1);
        _pipelines.add(pipeline2);
        
        _unitUnderTest = new RequestMarshaller(_pipelines);
        
        try
        {
            Pipeline pipeline = _unitUnderTest.pipeline("my/path");
            assertTrue(pipeline.isPipeline("my/path"));
            
            pipeline = _unitUnderTest.pipeline("my/other/path");
            assertTrue(pipeline.isPipeline("my/other/path"));
        }
        catch (Exception ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
}
