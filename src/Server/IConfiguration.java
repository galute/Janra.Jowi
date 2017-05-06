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
package Server;

/**
 *
 * @author jmillen
 */
public interface IConfiguration
{
    void setTimeout(Integer value); // in mS
    void setMaxThreads(Integer maxThreads);
    void setMaxUriLength(Integer maxThreads);
    void setDefaultCharsetIncoming(String charset);
    void addMiddleware(String path, IPipelineMiddleware middleware);
    void registerExceptionHandler(IExceptionHandler handler);
    long timeout();
    Integer maxThreads();
    Integer maxUriLength();
    String defaultCharset();
}
