/*
 *  Base58 library, a Java implementation of Base58 encode/decode
 *  Copyright (C) 2017 Alan Evans, NovaCrypto
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

package io.github.novacrypto;

import java.util.Arrays;

final class ByteArrayByteBuffer implements Base58.ByteBuffer {

    private static byte[] EMPTY = new byte[0];

    private byte[] bytes = EMPTY;

    @Override
    public void setCapacity(final int atLeast) {
        bytes = ensureCapacity(bytes, atLeast);
        clear(bytes);
    }

    @Override
    public byte get(final int index) {
        return bytes[index];
    }

    @Override
    public void put(final int index, final byte value) {
        bytes[index] = value;
    }

    @Override
    public void clear() {
        clear(bytes);
    }

    private static byte[] ensureCapacity(byte[] bytes, int atLeast) {
        if (bytes.length >= atLeast) {
            return bytes;
        }
        clear(bytes);
        return new byte[atLeast];
    }

    private static void clear(byte[] bytes) {
        Arrays.fill(bytes, (byte) 255);
    }
}