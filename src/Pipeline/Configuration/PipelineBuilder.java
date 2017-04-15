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

import Request.Processing.Pipeline;
import Request.Processing.PipelineModule;
import Server.IPipelineMiddleware;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jmillen
 */
public class PipelineBuilder implements IPipelineBuilder
{

    @Override
    public List<Pipeline> build(Map<String, Map<Integer, IPipelineMiddleware>> config)
    {
        List<Pipeline> pipelines = new ArrayList<>();
        
        Iterator routeIterator = config.entrySet().iterator();
        while (routeIterator.hasNext())
        {
            Map.Entry routePair = (Map.Entry)routeIterator.next();
            String path = (String)routePair.getKey();
            Map<Integer, IPipelineMiddleware>pipelineConfig = config.get(path);
            Iterator moduleIterator = pipelineConfig.entrySet().iterator();
            PipelineModule module = null;
            while (moduleIterator.hasNext())
            {
                Map.Entry pipelinePair = (Map.Entry)moduleIterator.next();
                IPipelineMiddleware middleware = (IPipelineMiddleware)pipelinePair.getValue();
                
                module = new PipelineModule(middleware, module);
                moduleIterator.remove();
            }
            
            Pipeline pipeline = new Pipeline(path, module);
            pipelines.add(pipeline);
            routeIterator.remove(); // avoids a ConcurrentModificationException
        }
        
        return pipelines;
    }
    
}
