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
package Protocol.Builders;

import Network.Readers.IdentityReader;
import Protocol.Models.HttpRequest;
import Network.Wrappers.ISocketChannel;
import Network.Readers.ChannelReader;
import Protocol.Models.*;
import Protocol.Parsers.*;
import Request.Processing.TransferEncodingFactory;
import Request.Processing.TransferEncodingProcessor;
import Server.IConfiguration;
import Server.IHeader;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 *
 * @author jmillen
 */
public class RequestBuilder implements IRequestBuilder
{
    private final IParser _parser;
    private final ChannelReader _reader;
    private String _charset;
    private final IConfiguration _config;
    
    public RequestBuilder(IParser parser, IConfiguration config)
    {
        _parser = parser;
        _charset = config.defaultCharset();
        _reader = new ChannelReader(_charset);
        _config = config;
    }
    
    @Override
    public HttpContext ProcessRequest(ISocketChannel channel)
    {
        try
        {
            String requestLine = new String(_reader.readLine(channel), _charset);
            HttpRequest request = _parser.ParseRequestLine(requestLine);
            
            Headers headers = getHeaders(channel);
            
            request.addHost(headers.get("host"));
            
            headers.remove("host");
            
            if (headers.get("content-range") != null &&
                request.method() == HttpMethod.PUT)
            {
                // rfc 7231 Sect 4.3.3
                // An origin server that allows PUT on a given target resource MUST send
                // a 400 (Bad Request) response to a PUT request that contains a
                // Content-Range header field
                throw new ProtocolException("Content-Range not allowed for PUT requests", 400);
            }
            
            
            if (request.host().length() + request.path().length() > _config.maxUriLength())
            {
                throw new ProtocolException("Max Uri length exceeded", 414);
            }
            
            ContentType contentType = processContentType(headers);
            request.setMediaType(contentType.mediaType());
            _charset = contentType.charset();
            request.setCharset(_charset);
            
            IHeader transferEncoding = headers.get("transfer-encoding");
            
            if (transferEncoding != null) // Transfer-encoding takes precidence
            {
                request.setBody(processEncoding(transferEncoding, channel));
            }
            else
            {
                IHeader contentLen = headers.get("content-length");

                if (contentLen != null)
                {
                    IdentityReader reader = new IdentityReader(contentLen, _charset);
                    request.setBody(new RequestBody(reader.getBody(channel)));
                }
                else if (request.method() != HttpMethod.GET)
                {
                    throw new ProtocolException("Both Transfer-Encoding and Content-Length missing", 400);
                }
            }
            
            request.addHeaders(headers);
            
            return new HttpContext(request);
        }
        catch (ProtocolException ex)
        {
            return new HttpContext(ex.ResponseStatus);
        }
        catch (IOException | URISyntaxException | IllegalArgumentException ex)
        {
            return new HttpContext(400);
        }
    }
    
    private ContentType processContentType(Headers headers) throws ProtocolException
    {
        IHeader contentTypeHeader = headers.get("content-type");
        if (contentTypeHeader != null)
        {
            return new ContentType(contentTypeHeader, _charset);
        }
        
        return new ContentType(_charset);
    }
    
    private RequestBody processEncoding(IHeader header, ISocketChannel channel) throws ProtocolException, IOException
    {
        Boolean chunkedSet = false;
        Integer occurences = header.occurences();
        
        for (int i = 0; i < occurences; i++)
        {
            if (header.value(i).equals("chunked"))
            {
                chunkedSet = true;
            }
            else if (chunkedSet)
            {
                throw new ProtocolException("Chunked must be last encoding", 400);
            }
        }
        
        TransferEncodingProcessor processor = TransferEncodingFactory.create();
        return processor.decode(channel, header, null);
    }
    
    private Headers getHeaders(ISocketChannel channel) throws ProtocolException, IOException
    {
        Boolean finished = false;
        Boolean hasHostHeader = false;
        Headers headers = new Headers();

        while (!finished)
        {
            String headerLine = new String(_reader.readLine(channel), _charset);
            if (headerLine.isEmpty())
            {
                finished = true;
            }
            else
            {
                IHeader header = _parser.ParseHeader(headerLine);
                if ("host".equals(header.key().toLowerCase()))
                {
                    if (hasHostHeader)
                    {
                        //rfc7230 section 5.4
                        // A server MUST respond with a 400 (Bad Request) status code to any
                        // HTTP/1.1 request message that lacks a Host header field and to any
                        // request message that contains more than one Host header field or a
                        // Host header field with an invalid field-value.
                        throw new ProtocolException("Multiple Host headers not allowed", 400);
                    }
                    else
                    {
                        hasHostHeader = true;
                        headers.addHeader(header);
                        int val = header.value().length();
                        int val2 = _config.maxUriLength();
                        if (header.value().length() > _config.maxUriLength())
                        {
                            throw new ProtocolException("Host exceed max Uri length", 414);
                        }
                    }
                }
                else if ("transfer-encoding".equals(header.key().toLowerCase()))
                {
                    headers.remove("content-length");
                    headers.addHeader(header);
                }
                else if ("content-length".equals(header.key().toLowerCase()) &&
                         headers.get("transfer-encoding") == null)
                {
                    headers.addHeader(header);
                }
                else
                {
                    headers.addHeader(header);
                }
            }
        }

        if (!hasHostHeader)
        {
            throw new ProtocolException("Host header is missing", 400);
        }

        return headers;
    }
}
