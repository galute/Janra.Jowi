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

import Server.*;

/**
 *
 * @author jmillen
 */
public class Configuration implements IConfiguration
{
    private long _timeout = 500;
    private Integer _maxThreads = 100;
    private Integer _maxUriLength = 1048;
    private String _defaultCharset = "ISO-8859-1"; // not actually default anymore (rfc 7231 Appendix B) but need something.
    private IExceptionHandler _handler;
    
    private final IPipelineConfiguration _pipelineConfig;
    
    public Configuration(IPipelineConfiguration config, IExceptionHandler handler)
    {
        _pipelineConfig = config;
        _handler = handler;
    }
    
    @Override
    public void setTimeout(Integer value)
    {
        _timeout = value;
    }
    
    @Override
    public void setMaxThreads(Integer maxThreads)
    {
        _maxThreads = maxThreads;
    }
    
    @Override
    public void setMaxUriLength(Integer maxUriLength)
    {
        _maxUriLength = maxUriLength;
    }
    
    @Override
    public void setDefaultCharsetIncoming(String charset)
    {
        _defaultCharset = charset;
    }
    
    @Override
    public void addMiddleware(String path, IPipelineMiddleware middleware)
    {
        _pipelineConfig.addMiddleware(path, middleware);
    }
    
    @Override
    public long timeout()
    {
        return _timeout;
    }
    
    @Override
    public Integer maxThreads()
    {
        return _maxThreads;
    }
    
    @Override
    public Integer maxUriLength()
    {
        return _maxUriLength;
    }
    
    public String defaultCharset()
    {
        return _defaultCharset;
    }
    
    @Override
    public void registerExceptionHandler(IExceptionHandler handler)
    {
        _handler = handler;
    }
    
    public IExceptionHandler handler()
    {
        return _handler;
    }
}
