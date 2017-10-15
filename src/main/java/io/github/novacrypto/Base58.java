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

/**
 * Class for encoding byte arrays to base58.
 * Suitable for small data arrays as the algorithm is O(n^2).
 */
public final class Base58 {

    public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private byte[] bytes;

    private static final ThreadLocal<Base58> working = new ThreadLocal<>();

    /**
     * Encodes given bytes as a number in base58.
     * Threadsafe, uses an instance per thread.
     * @param bytes bytes to encode
     * @return base58 string representation
     */
    public static CharSequence encodeStatic(byte[] bytes) {
        return getThreadSharedBase58().encode(bytes);
    }

    private static Base58 getThreadSharedBase58() {
        Base58 base58 = working.get();
        if (base58 == null) {
            base58 = new Base58();
            working.set(base58);
        }
        return base58;
    }

    /**
     * Encodes given bytes as a number in base58.
     * @param bytes bytes to encode
     * @return base58 string representation
     */
    public String encode(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        encode(bytes, sb::append);
        return sb.toString();
    }

    public interface Target {
        void append(char x);
    }

    private byte[] getBufferOfAtLeastBytes(final int atLeast) {
        if (bytes == null || bytes.length < atLeast) {
            if (bytes != null)
                Arrays.fill(bytes, (byte) 255);
            bytes = new byte[atLeast];
        }
        Arrays.fill(bytes, (byte) 255);
        return bytes;
    }

    /**
     * Encodes given bytes as a number in base58.
     * @param bytes bytes to encode
     * @param target where to write resulting string to
     */
    public void encode(final byte[] bytes, final Target target) {
        final char[] A = ALPHABET;
        final int bLen = bytes.length;
        final byte[] d = getBufferOfAtLeastBytes(bLen << 1);
        int dlen = -1;
        int blanks = 0;
        int j = 0;
        for (int i = 0; i < bLen; i++) {
            int c = bytes[i] & 0xff;
            if (c == 0 && blanks == i) {
                target.append(A[0]);
                blanks++;
            }
            j = 0;
            while (j <= dlen || c != 0) {
                int n;
                if (j > dlen) {
                    dlen = j;
                    n = c;
                } else {
                    n = d[j];
                    n = (n << 8) + c;
                }
                d[j] = (byte) (n % 58);
                c = n / 58;
                j++;
            }
        }
        while (j-- > 0) {
            target.append(A[d[j]]);
        }
        Arrays.fill(d, (byte) 255);
    }
}