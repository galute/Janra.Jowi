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
package Protocol.Models;

import Protocol.Parsers.ProtocolException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jmillen
 */
public class HttpStatus
{
    private static final Map<Integer, String> _codes;

    static {
        Map<Integer, String> tempMap = new HashMap<>();
        tempMap.put(100, "Continue");
        tempMap.put(101, "Switching Protocols");
        tempMap.put(200, "OK");
        tempMap.put(201, "Created");
        tempMap.put(202, "Accepted");
        tempMap.put(203, "Non-Authoritative Information");
        tempMap.put(204, "No Content");
        tempMap.put(205, "Reset Content");
        tempMap.put(206, "Partial Content");
        tempMap.put(300, "Multiple Choices");
        tempMap.put(301, "Moved Permanently");
        tempMap.put(302, "Found");
        tempMap.put(303, "See Other");
        tempMap.put(304, "Not Modified");
        tempMap.put(305, "Use Proxy");
        tempMap.put(307, "Temporary Redirect");
        tempMap.put(400, "Bad Request");
        tempMap.put(401, "Unauthorized");
        tempMap.put(402, "Payment Required");
        tempMap.put(403, "Forbidden");
        tempMap.put(404, "Not Found");
        tempMap.put(405, "Method Not Allowed");
        tempMap.put(406, "Not Acceptable");
        tempMap.put(407, "Proxy Authentication Required");
        tempMap.put(408, "Request Time-out");
        tempMap.put(409, "Conflict");
        tempMap.put(410, "Gone");
        tempMap.put(411, "Length Required");
        tempMap.put(412, "Precondition Failed");
        tempMap.put(413, "Request Entity Too Large");
        tempMap.put(414, "Request-URI Too Large");
        tempMap.put(415, "Unsupported Media Type");
        tempMap.put(416, "Requested range not satisfiable");
        tempMap.put(417, "Expectation Failed");
        tempMap.put(500, "Internal Server Error");
        tempMap.put(501, "Not Implemented");
        tempMap.put(502, "Bad Gateway");
        tempMap.put(503, "Service Unavailable");
        tempMap.put(504, "Gateway Time-out");
        tempMap.put(505, "HTTP Version not supported");
        _codes = Collections.unmodifiableMap(tempMap);
    }
    
    public static String getRaw(Integer status) throws ProtocolException
    {
        if (!_codes.containsKey(status))
        {
            throw new ProtocolException("Status code " + status.toString() + " not recognised");
        }
        
        return MessageFormat.format("{0} {1}", status.toString(), _codes.get(status));
    }
}
