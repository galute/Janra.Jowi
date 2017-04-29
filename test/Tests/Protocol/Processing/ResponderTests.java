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
package Tests.Protocol.Processing;

import Protocol.Models.ResponseImpl;
import Protocol.Parsers.ProtocolException;
import Request.Processing.Responder;
import Tests.Stubs.Protocol.ResponseBuilderStub;
import Tests.Stubs.Network.SocketStubComplete;
import java.io.IOException;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class ResponderTests
{
    private Responder _unitUnderTest;
    private ResponseBuilderStub _builder;
    private SocketStubComplete _socket;
    
    @Before
    public void setup()
    {
        _socket = new SocketStubComplete();
        _builder = new ResponseBuilderStub();
        _unitUnderTest = new Responder(_builder);
    }
    
    @Test
    public void DoesNotReCallSocketIfAllDataWritten()
    {
        try
        {
            _socket.setBytesToWrite(10);
            _builder.setResponse("1234567890");
            _unitUnderTest.sendResponse(new ResponseImpl(), _socket);
            assertTrue(_socket.NumWrites == 1);
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Exception thrown: " + ex.getMessage());
        }
    }
    
    @Test
    public void ReCallsSocketUntilAllDataWritten()
    {
        try
        {
            _socket.setBytesToWrite(2);
            _builder.setResponse("1234567890");
            _unitUnderTest.sendResponse(new ResponseImpl(), _socket);
            assertTrue(_socket.NumWrites == 5);
        }
        catch (ProtocolException | IOException ex)
        {
            fail("Exception thrown: " + ex.getMessage());
        }
    }
}
