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
package NetworkReaders;

import java.util.zip.Inflater;
import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 *
 * @author jmillen
 */
public class GzipReader
{
    public byte[] doit(byte[] zipped) throws IOException
    {
        Integer result = 0;
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(zipped);
        GZIPInputStream  gzippedIn = new GZIPInputStream(bytesIn);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[1024];
        
        while (result >= 0)
        {
            result = gzippedIn.read(buffer, 0, buffer.length);
            
            if (result > 0)
            {
                bytesOut.write(buffer, 0, result);
            }
        }
        
        return bytesOut.toByteArray();
    }
}
