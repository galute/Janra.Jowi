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
package Request.Processing;

import Pipeline.Configuration.InvalidConfigurationException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jmillen
 */
public class RequestMarshaller implements IMarshaller
{
    private final List<Pipeline> _pipelines;
    
    public RequestMarshaller(List<Pipeline> pipelines)
    {
        _pipelines = pipelines;
    }
    
    @Override
    public IPipeline pipeline(String path) throws InvalidConfigurationException
    {
        List<Pipeline>candidates;
        
        candidates = new ArrayList<>();
        
        _pipelines.forEach((k)->{
	
            if(k.isPipeline(path))
            {
                candidates.add(k);
            }
        });
        
        if (candidates.size() > 1)
        {
            /* 
             * This should never happen, registration is based on a HashMap
             * with the path as the key
             */
            throw new InvalidConfigurationException("Multiple registrations of same path");
        }
        
        if (candidates.isEmpty())
        {
            return null;
        }
        return candidates.get(0);
    }
}
