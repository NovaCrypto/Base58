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

import static io.github.novacrypto.base58.Base58.base58Encode;
import static io.github.novacrypto.base58.CapacityEstimator.estimateMaxLength;
import static org.junit.Assert.assertEquals;

public final class CapacityEstimatorTests {

    @Test
    public void constructor_for_coverage() {
        new CapacityEstimator();
    }

    @Test
    public void capacity_of_low_numbers() {
        for (int byteLength = 0; byteLength < 512; byteLength++) {
            assertEquals(actualMaxLengthOfBytes(byteLength), estimateMaxLength(byteLength));
        }
    }

    @Test
    public void capacity_of_high_numbers() {
        for (int byteLength = 2000; byteLength < 2050; byteLength++) {
            assertEquals(actualMaxLengthOfBytes(byteLength), estimateMaxLength(byteLength));
        }
    }

    private static int actualMaxLengthOfBytes(int byteLength) {
        return base58Encode(getBytesWithMaxValue(byteLength)).length();
    }

    private static byte[] getBytesWithMaxValue(int byteLength) {
        final byte[] bytes = new byte[byteLength];
        for (int i = 0; i < byteLength; i++) {
            bytes[i] = (byte) 255;
        }
        return bytes;
    }

}
