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
package Tests.Protocol.Models;

import Protocol.Models.HttpStatus;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author jmillen
 */
public class HttpStatusTests
{
    @Test
    public void IsInformationalTrueFor1xx()
    {
        for (Integer status = 100; status < 200; status++)
        {
            assertTrue(HttpStatus.isInformational(status));
        }
    }
    
    @Test
    public void isInformationFalseFor2xx3xx4xxAnd5xx()
    {
        for (Integer status = 200; status < 600; status++)
        {
            assertFalse(HttpStatus.isInformational(status));
        }
    }
}
