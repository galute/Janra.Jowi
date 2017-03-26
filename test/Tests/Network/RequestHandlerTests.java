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
import Tests.Network.Stubs.SelectorKeyStub;
import Tests.Parsers.Stubs.ParserStub;
import Tests.Protocol.Stubs.RequestProcessorStub;
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
    RequestProcessorStub _processor;
    
    public void WhenRunningHandler(SelectorKeyStub keyStub)
    {
        _unitUnderTest = new RequestHandler(_keyStub, _processor);
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
        _processor = new RequestProcessorStub();
    }
    @Test
    public void CancelsKeyIfNotReadable()
    {
        WhenSelectorKeyFlagsAreSet(false, false);
        _unitUnderTest = new RequestHandler(_keyStub, _processor);
        _unitUnderTest.run();
        
        assertTrue(_keyStub.IsCancelled);
    }
}
