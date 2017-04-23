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

import Network.Wrappers.ISocketChannel;
import Protocol.Models.*;
import Protocol.Parsers.*;
import Server.IHeader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 *
 * @author jmillen
 */
public class RequestBuilder implements IRequestBuilder
{
    private final CharsetDecoder _decoder;
    private final IParser _parser;
    
    public RequestBuilder(IParser parser)
    {
        _parser = parser;
        _decoder = Charset.forName("ISO-8859-1").newDecoder();
    }
    
    @Override
    public HttpContext ProcessRequest(ISocketChannel channel)
    {
        try
        {
            String requestLine = readLine(channel);
            HttpRequest request = _parser.ParseRequestLine(requestLine);
            
            Boolean finished = false;
            Boolean hasHostHeader = false;
            Headers headers = new Headers();
            
            while (!finished)
            {
                String headerLine = readLine(channel);
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
                            request.addHost(header);
                        }
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
            
            request.addHeaders(headers);
            
            return new HttpContext(request);
        }
        catch (ProtocolException ex)
        {
            // Todo pass ex message for response body?
            return new HttpContext(ex.ResponseStatus);
        }
        catch (IOException ex)
        {
            return new HttpContext(400);
        }
    }
    
    public String readLine(ISocketChannel channel) throws IOException, ProtocolException
    {
        Boolean endOfLine = false;
        Boolean endStart = false;
        CharBuffer buffer = CharBuffer.allocate(2048);
        
        while (!endOfLine)
        {
            ByteBuffer bbuffer = ByteBuffer.allocate(1);

            int szRead = channel.read(bbuffer);
            
            if (szRead < 1)
            {
                throw new ProtocolException("Unable to process incomplete data", 400);
            }
            
            bbuffer.flip();
        
            
            CharBuffer buff = _decoder.decode(bbuffer);
            
            char value = buff.charAt(0);
            
            switch (value)
            {
                case '\r':
                    endStart = true;
                    break;
                case '\n':
                    if (endStart)
                    {
                        endOfLine = true;
                    }
                    else
                    {
                        throw new ProtocolException("Unexpected new line character", 400);
                    }
                    break;
                default:
                    endStart = false;
                    buffer = buffer.put(value);
            }
        }
        buffer.flip();
        return new StringBuilder(buffer).toString();
    }
}
