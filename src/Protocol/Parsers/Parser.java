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

//GET / HTTP/1.1
//Host: localhost:6543
//Connection: keep-alive
//Upgrade-Insecure-Requests: 1
//User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36
//Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
//Accept-Encoding: gzip, deflate, sdch, br
//Accept-Language: en-GB,en-US;q=0.8,en;q=0.6
//Cookie: Rider-12145af8=199fbc8c-1b75-4bb4-8575-f91111b19480
package Protocol.Parsers;

import Protocol.Models.HttpRequest;

/**
 *
 * @author jmillen
 */
public class Parser implements IParser
{

    @Override
    public HttpRequest Parse(String buffer) throws ProtocolException
    {
        String[] lines;
        lines = buffer.split("\\r?\\n");
        
        if (lines.length < 1)
        {
            throw new ProtocolException("Line 1 missing of incoming Request");
        }
        
        String[] line1;
        line1 = ProtocolChecker(lines[0]);
        
        HttpRequest request = new HttpRequest();
        
        request.method = line1[0];
        request.path = line1[1];
        request.version = line1[2];
        
        return request;
    }
    
    private String[] ProtocolChecker(String line1)
    {
        String[] elements;
        
        elements = line1.split(" ");
        
        return elements;
        
    }
    
}
