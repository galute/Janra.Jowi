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
import Network.Wrappers.ISelectorKey;
import Protocol.Builders.IRequestBuilder;
import Protocol.Builders.IResponseBuilder;
import Protocol.Builders.RequestBuilder;
import Protocol.Builders.ResponseBuilder;
import Protocol.Parsers.IParser;
import Protocol.Parsers.Parser;
import Request.Processing.IMarshaller;
import Request.Processing.IProcessRequest;
import Request.Processing.ISendResponse;
import Request.Processing.RequestProcessor;
import Request.Processing.Responder;

/**
 *
 * @author jmillen
 */
public class RequestHandlerFactory
{
    static public RequestHandler Create(ISelectorKey key, IMarshaller marshaller)
    {
        IParser parser = new Parser();
        IResponseBuilder responseBuilder = new ResponseBuilder();
        IRequestBuilder requestBuilder = new RequestBuilder(parser);

        IProcessRequest requestProcessor = new RequestProcessor(marshaller);
        ISendResponse responder = new Responder(responseBuilder);
        return new RequestHandler(key, requestBuilder, requestProcessor, responder);
    }
}
