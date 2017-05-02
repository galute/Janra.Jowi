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
package Tests.Network.Readers;

import Network.Readers.GzipReader;
import Protocol.Parsers.ProtocolException;
import Tests.Stubs.Network.SocketStubBinary;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class GzipReaderTests
{
    @Test
    public void UnzipsData()
    {
        try
        {
            GzipReader reader = new GzipReader();
            String dataStr = "This is a string of test data";
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(dataStr.getBytes("UTF-8"));
            gzip.close();
            out.close();
            byte[] zippedData = out.toByteArray();
            
            byte[] unzipped = reader.processData(zippedData, new SocketStubBinary());
            String result = new String(unzipped, "UTF-8");
            assertTrue(dataStr.equals(result));
        }
        catch (IOException | ProtocolException ex)
        {
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
}
