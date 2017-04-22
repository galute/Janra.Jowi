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
package Network.Factories;

import Network.Handlers.RequestHandler;
import Network.Wrappers.*;
import Protocol.Builders.*;
import Protocol.Parsers.*;
import Request.Processing.*;
import Utilities.ILauncher;
import java.io.IOException;

/**
 *
 * @author jmillen
 */
public class RequestHandlerFactory
{
    static public Runnable create(ISocketChannel channel, IMarshaller marshaller, long timeout, ILauncher launcher) throws IOException
    {
        IParser parser = new Parser();
        ISelector selector = new SelectorWrapper();
        IResponseBuilder responseBuilder = new ResponseBuilder();
        IRequestBuilder requestBuilder = new RequestBuilder(parser);

        IProcessRequest requestProcessor = new RequestProcessor(marshaller);
        ISendResponse responder = new Responder(responseBuilder);
        return new RequestHandler(selector, channel, requestBuilder, requestProcessor, responder, timeout, launcher);
    }
}
