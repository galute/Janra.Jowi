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
package Protocol.Models;

import java.util.Map;

/**
 *
 * @author jmillen
 */
public class HttpRequest
{
    private final HttpMethod _method;
    private final String _path;
    private final String _version;
    private final String _host;
    private final Map<String,String> _headers;
    
    public HttpRequest(HttpMethod method, String path, String version, String host, Map<String,String> headers)
    {
        _method = method;
        _path = path;
        _version = version;
        _host = host;
        _headers = headers;
    }
    
    public HttpMethod method()
    {
        return _method;
    }
    
    public String path()
    {
        return _path;
    }
    
    public String version()
    {
        return _version;
    }
    
    public String host()
    {
        return _host;
    }
    
    public String header(String name)
    {
        if (_headers.containsKey(name))
        {
            return _headers.get(name);
        }
        return null;
    }
 }
