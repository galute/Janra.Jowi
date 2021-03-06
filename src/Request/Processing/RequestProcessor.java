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

import Pipeline.Configuration.Configuration;
import Pipeline.IPipeline;
import Pipeline.Configuration.InvalidConfigurationException;
import Protocol.Models.HttpContext;

/**
 *
 * @author jmillen
 */
public class RequestProcessor implements IProcessRequest
{
    private final IMarshaller _marshaller;
    private final Configuration _config;
    
    public RequestProcessor(IMarshaller marshaller, Configuration config)
    {
        _marshaller = marshaller;
        _config = config;
    }
    
    @Override
    public HttpContext processRequest(HttpContext context)
    {
        try
        {
            IPipeline pipeline = _marshaller.pipeline(context.request().path());
            
            if (pipeline == null)
            {
                context.response().setStatus(404);
                return context;
            }
            
            pipeline.run(new RequestContext(context));
            
            return context;
        }
        catch (InvalidConfigurationException ex)
        {
            _config.handler().HandleException(ex);
        }
        
        context.response().setStatus(500);
        return context;
    }
    
}
