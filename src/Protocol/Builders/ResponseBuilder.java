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

import Protocol.Models.HttpResponse;
import Protocol.Models.HttpStatus;
import Protocol.Parsers.ProtocolException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 *
 * @author jmillen
 */
public class ResponseBuilder implements IResponseBuilder
{
    private final CharsetEncoder _encoder;
    
    public ResponseBuilder()
    {
        _encoder = Charset.forName("ISO-8859-1").newEncoder();
    }
    
    @Override
    public ByteBuffer BuildResponse(HttpResponse response) throws ProtocolException, CharacterCodingException
    {
        //rfc7230 section-3.1.2
        String statusLine = "HTTP/1.1 " + HttpStatus.getRaw(response.status()) + "\r\nServer: Jowi\r\n";
        
        return _encoder.encode(CharBuffer.wrap(statusLine + response.getRaw()));
    } 
}
