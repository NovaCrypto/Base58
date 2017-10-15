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

    private static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private byte[] bytes;

    private static final ThreadLocal<Base58> working = new ThreadLocal<>();

    /**
     * Encodes given bytes as a number in base58.
     * Threadsafe, uses an instance per thread.
     *
     * @param bytes bytes to encode
     * @return base58 string representation
     */
    public static CharSequence encodeStatic(byte[] bytes) {
        return getThreadSharedBase58().encode(bytes);
    }

    /**
     * Decodes given bytes as a number in base58.
     * Threadsafe, uses an instance per thread.
     *
     * @param base58 string to decode
     * @return number as bytes
     */
    public static byte[] decodeStatic(final CharSequence base58) {
        return getThreadSharedBase58().decode(base58);
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
     *
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
     *
     * @param bytes  bytes to encode
     * @param target where to write resulting string to
     */
    public void encode(final byte[] bytes, final Target target) {
        final char[] a = ALPHABET;
        final int bLen = bytes.length;
        final byte[] d = getBufferOfAtLeastBytes(bLen << 1);
        int dlen = -1;
        int blanks = 0;
        int j = 0;
        for (int i = 0; i < bLen; i++) {
            int c = bytes[i] & 0xff;
            if (c == 0 && blanks == i) {
                target.append(a[0]);
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
            target.append(a[d[j]]);
        }
        Arrays.fill(d, (byte) 255);
    }

    /**
     * Decodes given bytes as a number in base58.
     *
     * @param base58 string to decode
     * @return number as bytes
     */
    public byte[] decode(final CharSequence base58) {
        final char[] a = ALPHABET;
        final int strLen = base58.length();
        final byte[] d = getBufferOfAtLeastBytes(strLen);
        int dlen = -1;
        int blanks = 0;
        int j = 0;
        for (int i = 0; i < strLen; i++) {
            j = 0;
            int c = indexOf(a, base58.charAt(i));
            if (c < 0)
                throw new RuntimeException();
            if (c == 0 && blanks == i) {
                blanks++;
            }
            while (j <= dlen || c != 0) {
                int n;
                if (j > dlen) {
                    dlen = j;
                    n = c;
                } else {
                    n = d[j] & 0xff;
                    n = n * 58 + c;
                }
                d[j] = (byte) n;
                c = n >>> 8;
                j++;
            }
        }
        final byte[] bytes = new byte[j + blanks];
        final int end = j + blanks - 1;
        for (int i = blanks; i < bytes.length; i++) {
            bytes[i] = d[end - i];
        }
        Arrays.fill(d, (byte) 255);
        return bytes;
    }

    private int indexOf(char[] a, char c) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == c) return i;
        }
        return -1;
    }
}