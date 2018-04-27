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

final class CapacityEstimator {

    CapacityEstimator() {
    }

    private static final double log2_58 = Math.log(58) / Math.log(2);

    private static final double storageRatio = 8.0 / log2_58;

    /**
     * Estimates max length of base58 string using formula:
     * <p>
     * maxLength characters = length bytes * 8 bits per byte / Log2(58) bits per character
     */
    static int estimateMaxLength(int byteLength) {
        return (int) Math.ceil(byteLength * storageRatio);
    }
}