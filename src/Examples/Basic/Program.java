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
package Examples.Basic;

import Server.IConfiguration;
import Server.IPipelineMiddleware;
import Server.Server;
import java.io.IOException;

/**
 *
 * @author jmillen
 */
public class Program
{
    public static void main(String[] args) throws IOException, Exception
    {
        IPipelineMiddleware middleware = new Pong();
        Server server = new Server();
        IConfiguration config = server.create();
        config.setTimeout(500); //mS
        config.addMiddleware("/my/path", middleware);
        
        server.Start(6543, config);
    }
}
