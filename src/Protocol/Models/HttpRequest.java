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

import Server.IHeader;

/**
 *
 * @author jmillen
 */
public class HttpRequest implements Cloneable
{
    private final HttpMethod _method;
    private final String _path;
    private final String _version;
    private String _host = "";
    private Headers _headers;
    
    public HttpRequest(HttpMethod method, String path, String version)
    {
        _method = method;
        _path = path;
        _version = version;
        _host = "";
        _headers = new Headers();
    }
    
    public HttpRequest(HttpRequest request)
    {
        _method = request._method;
        _path = request._path;
        _version = request._version;
        _host = request._host;
        _headers = new Headers(request._headers);
    }
    
    public void addHost(IHeader hostHeader)
    {
        _host = hostHeader.value(0);
    }
    
    public void addHeaders(Headers headers)
    {
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
    
    public IHeader header(String name)
    {
        return _headers.get(name);
    }
 }
