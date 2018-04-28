/*
 *  Base58 library, a Java implementation of Base58 encode/decode
 *  Copyright (C) 2017-2018 Alan Evans, NovaCrypto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  Original source: https://github.com/NovaCrypto/Base58
 *  You can contact the authors via github issues.
 */

package io.github.novacrypto.base58;

import org.junit.Test;

import static io.github.novacrypto.base58.CapacityCalculator.maximumBase58StringLength;
import static io.github.novacrypto.base58.CapacityCalculatorTests.getBytesWithMaxValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class EncodeTargetCapacitySetTests {

    static class CapacityTargetSpy implements EncodeTargetCapacity, EncodeTarget {

        private boolean beforeAppend = true;
        int capacityOnFirstAppend;

        @Override
        public void setCapacity(final int characters) {
            assertTrue(beforeAppend);
            this.capacityOnFirstAppend = characters;
        }

        @Override
        public void append(final char c) {
            beforeAppend = false;
        }
    }

    static class CapacityTargetSpyFromCapacity implements EncodeTargetFromCapacity {

        int capacitySet;
        final StringBuilder sb = new StringBuilder();

        @Override
        public EncodeTarget withCapacity(final int characters) {
            capacitySet = characters;
            return sb::append;
        }
    }

    @Test
    public void sets_capacity_3bytes() {
        final CapacityTargetSpy spy = new CapacityTargetSpy();
        Base58.newSecureInstance().encode(getBytesWithMaxValue(3), spy, spy);
        assertEquals(maximumBase58StringLength(3), spy.capacityOnFirstAppend);
    }

    @Test
    public void sets_capacity_10bytes() {
        final CapacityTargetSpy spy = new CapacityTargetSpy();
        Base58.newSecureInstance().encode(new byte[10], spy, spy);
        assertEquals(maximumBase58StringLength(10), spy.capacityOnFirstAppend);
    }

    @Test
    public void sets_capacity_3bytes_single_argument_overload() {
        final CapacityTargetSpyFromCapacity spy = new CapacityTargetSpyFromCapacity();
        Base58.newSecureInstance().encode(getBytesWithMaxValue(3), spy);
        assertEquals(maximumBase58StringLength(3), spy.capacitySet);
    }

    @Test
    public void sets_capacity_10bytes_single_argument_overload() {
        final CapacityTargetSpyFromCapacity spy = new CapacityTargetSpyFromCapacity();
        Base58.newSecureInstance().encode(new byte[10], spy);
        assertEquals(maximumBase58StringLength(10), spy.capacitySet);
    }
}