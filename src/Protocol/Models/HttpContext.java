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

import Server.HttpResponse;

/**
 *
 * @author jmillen
 */
public class HttpContext
{
    private final HttpRequest _request;
    private final ResponseImpl _response;
    
    public HttpContext(Integer status)
    {
        _request = null;
        _response = new ResponseImpl(status);
    }
    
    public HttpContext(HttpRequest request)
    {
        _request = request;
        _response = new ResponseImpl();
        
        if (_request.method() == HttpMethod.CONNECT)
        {
            _response.bodyIsInvalid();
        }
        
        if (_request.method() == HttpMethod.HEAD)
        {
            _response.isHeadRequest();
        }
    }
    
    public HttpRequest request()
    {
        if (_request != null)
        {
            return new HttpRequest(_request);
        }
        return _request;
    }
    
    public HttpResponse response()
    {
        return _response;
    }
}
