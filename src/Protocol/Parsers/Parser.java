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

import Protocol.Models.*;
import java.util.HashMap;
import java.util.Map;

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
        lines = buffer.split("\\R", -1);
        
        if (lines.length < 3) // must end with 2 blanks lines if just request line
        {
            throw new ProtocolException("Request line missing from incoming Request");
        }
        
        String[] line1;
        line1 = ExtractRequestLine(lines[0]);
        
        HttpMethod method = HttpMethod.find(line1[0]);
        String path = line1[1];
        // version not always present so check
        String version = line1.length > 2 ? line1[2] : null;
        String host = ExtractHost(lines.length > 1 ? lines[1] : null);
        
        Map<String, String> headers = ExtractHeaders(lines, host == null ? 1 : 2);
        HttpRequest request = new HttpRequest(method, path, version, host, headers);
        
        return request;
    }
    
    private String[] ExtractRequestLine(String line1) throws ProtocolException
    {
        String[] elements;
        
        elements = line1.split(" ");
        
        if (elements.length < 2)
        {
            // Request-Line normally = Method SP Request-URI SP HTTP-Version CRLF
            // but can be Method SP Request-URI
            throw new ProtocolException("Invalid Request line");
        }
        
        return elements;
        
    }
    
    private String ExtractHost(String line2)
    {
        if (line2 == null)
        {
            return null;
        }

        String[] elements;
        
        elements = line2.split(": ");
        
        if ("Host".equals(elements[0]) && elements.length == 2)
        {
            return elements[1];
        }
        
        return null;
    }
    
    private Map<String, String> ExtractHeaders(String[] lines, int startIdx)
    {
        Map<String, String>headers = new HashMap<>();
        int idx = startIdx;
        
        if (lines.length < startIdx)
        {
            return headers;
        }
        
        while(!lines[idx].isEmpty())
        {
            String[] elements;
            elements = lines[idx].split(": ");
            if (elements.length !=2)
            {
                continue;
            }
            headers.put(elements[0], elements[1]);
            idx++;
        }
        
        return headers;
    }
    
}
