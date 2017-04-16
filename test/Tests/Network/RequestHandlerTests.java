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
package Tests.Network;

import Network.Handlers.RequestHandler;
import Tests.Stubs.Network.SelectorKeyStub;
import Tests.Stubs.Network.SelectorStub;
import Tests.Stubs.Network.SocketStubComplete;
import Tests.Stubs.Processing.ProcessRequestStub;
import Tests.Stubs.Processing.SendResponseStub;
import Tests.Stubs.Protocol.RequestBuilderStub;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class RequestHandlerTests
{
    private RequestHandler _unitUnderTest;
    private SelectorKeyStub _keyStub;
    private SelectorStub _selectorStub;
    private SocketStubComplete _socketStub;
    private RequestBuilderStub _builder;
    private ProcessRequestStub _processor;
    private SendResponseStub _responder;
    private final long _timeout = 500;
    
    
    public void WhenRunningHandler(SelectorKeyStub keyStub)
    {
        try
        {
            _unitUnderTest = new RequestHandler(_selectorStub, _socketStub, _builder, _processor, _responder, _timeout);
            Thread thread = new Thread(_unitUnderTest);
            thread.start();
        }
        catch (Exception ex)
        {
            fail("Throws unexpected exception: " + ex.getMessage());
        }
    }
    
    public void WhenSelectorKeyFlagsAreSet(Boolean acceptable, Boolean readable, Boolean writeable)
    {
        _selectorStub._setAcceptable = acceptable;
        _selectorStub._setReadable = readable;
        _selectorStub._setWriteable = writeable;
        _selectorStub._numKeysToSelect = 1;
    }
    
    @Before
    public void Setup()
    {
        _builder = new RequestBuilderStub();
        _processor = new ProcessRequestStub();
        _responder = new SendResponseStub();
        _selectorStub = new SelectorStub();
        _socketStub = new SocketStubComplete();
    }
    
    @Test
    public void RegistersKeyWithSelector()
    {
        try
        {
            WhenSelectorKeyFlagsAreSet(false, false, false);
            _unitUnderTest = new RequestHandler(_selectorStub, _socketStub, _builder, _processor, _responder, _timeout);
            assertTrue(_selectorStub._registeredForRead == 1);
        }
        catch (Exception ex)
        {
            fail("Throws unexpected exception: " + ex.getMessage());
        }
    }
    
    @Test
    public void CancelsKeyIfNotReadable()
    {
        try
        {
            WhenSelectorKeyFlagsAreSet(false, false, false);
            _unitUnderTest = new RequestHandler(_selectorStub, _socketStub, _builder, _processor, _responder, _timeout);
            _unitUnderTest.run();

            assertTrue(_processor.numRequests() == 0);
            assertTrue(_responder.numRequests() == 0);
        }
        catch (Exception ex)
        {
            fail("Throws unexpected exception: " + ex.getMessage());
        }
    }
    
    @Test
    public void DoesNotAcceptInValidRequest()
    {
        try
        {
            WhenSelectorKeyFlagsAreSet(false, true, true);
            _builder.Status = 400;
            _unitUnderTest = new RequestHandler(_selectorStub, _socketStub, _builder, _processor, _responder, _timeout);
            _unitUnderTest.run();
            assertTrue(_processor.numRequests() == 0);
            assertTrue(_responder.numRequests() == 1);
        }
        catch (Exception ex)
        {
            fail("Throws unexpected exception: " + ex.getMessage());
        }
    }
    
    @Test
    public void AcceptsValidRequest()
    {
        try
        {
            WhenSelectorKeyFlagsAreSet(false, true, true);
            _builder.Status = 200;
            _unitUnderTest = new RequestHandler(_selectorStub, _socketStub, _builder, _processor, _responder, _timeout);
            _unitUnderTest.run();
            assertTrue(_processor.numRequests() == 1);
            assertTrue(_responder.numRequests() == 1);
        }
        catch (Exception ex)
        {
            fail("Throws unexpected exception: " + ex.getMessage());
        }
    }
}
