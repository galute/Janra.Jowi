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
import Protocol.Parsers.IParser;
import Tests.Network.Stubs.SelectorKeyStub;
import Tests.Parsers.Stubs.ParserStub;
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
    ParserStub _parser;
    
    public void WhenRunningHandler(SelectorKeyStub keyStub)
    {
        _parser = new ParserStub();
        _unitUnderTest = new RequestHandler(keyStub, _parser, 10);
        Thread thread = new Thread(_unitUnderTest);
        thread.start();
    }
    
    @Before
    public void Setup()
    {
        _parser = new ParserStub();
        _keyStub = new SelectorKeyStub(false, false);
        _unitUnderTest = new RequestHandler(_keyStub, _parser, 10);
    }
    @Test
    public void CancelsKeyIfNotReadable()
    {
        _unitUnderTest = new RequestHandler(_keyStub, _parser, 10);
        _unitUnderTest.run();
        
        assertTrue(_keyStub.IsCancelled);
    }
    
    @Test
    public void KeepsReadingIfReadable() throws InterruptedException
    {
        _keyStub = new SelectorKeyStub(false, true);
        _keyStub.SocketStub.BytesToRead = 10;
        WhenRunningHandler(_keyStub);
        Thread.sleep(100);
        _keyStub.IsReadable = false;
        
        assertTrue(_keyStub.SocketStub != null);
        assertTrue(_keyStub.SocketStub.NumReads > 0);
        assertTrue(_parser.PassedBuffer.compareTo("XXXXXXXXXX") == 0);
    }
}
