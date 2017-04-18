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
package Pipeline.Configuration;

import Pipeline.IPipeline;
import Server.IPipelineConfiguration;
import Server.IPipelineMiddleware;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author jmillen
 */
public class PipelineConfiguration implements IPipelineConfiguration
{
    private final Map<String, Map<Integer, IPipelineMiddleware>> _pipelines = new HashMap<>();
    private final Map<String,Integer> _counters = new HashMap<>();
    private final IPipelineBuilder _builder;
    
    public PipelineConfiguration(IPipelineBuilder builder)
    {
        _builder = builder;
    }
    
    @Override
    public void addMiddleware(String path, IPipelineMiddleware middleware)
    {
        if (middleware == null || path == null || path.isEmpty())
        {
            throw new IllegalArgumentException("middleware and/or path invalid");
        }
        
        if (_pipelines.containsKey(path))
        {
            Integer counter = _counters.get(path);
            counter++;
            _pipelines.get(path).put(counter, middleware);
            _counters.replace(path, counter);
        }
        else
        {
            Map<Integer,IPipelineMiddleware>pipeline = new TreeMap(Collections.reverseOrder());
            pipeline.put(1, middleware);
            _pipelines.put(path, pipeline);
            _counters.put(path, 1);
        }
    }
    
    public List<IPipeline> build()
    {
        return _builder.build(_pipelines);
    }
}
