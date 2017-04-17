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

import Protocol.Models.Header;
import Protocol.Models.HttpContext;
import Protocol.Models.HttpRequest;
import Protocol.Models.HttpResponse;
import Server.IContext;
import Server.IProperties;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jmillen
 */
public class RequestContext implements IContext
{
    HttpContext _context;
    private final Map<String, Object> _properties = new HashMap<>();
    
    public RequestContext(HttpContext context)
    {
        _context = context;
    }
     
    @Override
    public HttpRequest Request()
    {
        return _context.request();
    }
    @Override
    public void setResponseStatus(Integer status)
    {
        _context.response().setStatus(status);
    }
    @Override
    public void setResponseBody(String body)
    {
        _context.response().setBody(body);
    }
    @Override
    public void addResponseHeader(String key, String value)
    {
       _context.response().addHeader(Header.create(key, value));
    }
    @Override
    public IProperties Properties()
    {
        return new ContextProperties(this);
    }
    
    public Object getProperty(String key)
    {
        return _properties.get(key);
    }
    
    public void addProperty(String key, Object value)
    {
        _properties.put(key, value);
    }
    
    public HttpResponse getResponse()
    {
        return _context.response();
    }
}
