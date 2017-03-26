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
import Tests.Stubs.Parsers.ParserStub;
import Tests.Stubs.Processing.ProcessRequestStub;
import Tests.Stubs.Protocol.RequestBuilderStub;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class RequestHandlerTests
{
    RequestHandler _unitUnderTest;
    SelectorKeyStub _keyStub;
    RequestBuilderStub _builder;
    ProcessRequestStub _processor;
    
    public void WhenRunningHandler(SelectorKeyStub keyStub)
    {
        _unitUnderTest = new RequestHandler(_keyStub, _builder, _processor);
        Thread thread = new Thread(_unitUnderTest);
        thread.start();
    }
    
    public void WhenSelectorKeyFlagsAreSet(Boolean acceptable, Boolean readable)
    {
        _keyStub = new SelectorKeyStub(acceptable, readable);
    }
    
    @Before
    public void Setup()
    {
        _builder = new RequestBuilderStub();
        _processor = new ProcessRequestStub();
    }
    @Test
    public void CancelsKeyIfNotReadable()
    {
        WhenSelectorKeyFlagsAreSet(false, false);
        _unitUnderTest = new RequestHandler(_keyStub, _builder, _processor);
        _unitUnderTest.run();
        
        assertTrue(_keyStub.IsCancelled);
    }
    
    @Test
    public void DoesNotAcceptInValidRequest()
    {
        WhenSelectorKeyFlagsAreSet(false, true);
        _builder.Status = 400;
        _unitUnderTest = new RequestHandler(_keyStub, _builder, _processor);
        _unitUnderTest.run();
        assertTrue(_processor.numRequests() == 0);
    }
    
    @Test
    public void AcceptsValidRequest()
    {
        WhenSelectorKeyFlagsAreSet(false, true);
        _builder.Status = 200;
        _unitUnderTest = new RequestHandler(_keyStub, _builder, _processor);
        _unitUnderTest.run();
        assertTrue(_processor.numRequests() == 1);
    }
}
