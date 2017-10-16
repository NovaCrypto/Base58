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
 * Don't share instances across threads.
 * Static methods are threadsafe however.
 */
public final class Base58 {

    public static final class BadCharacterException extends RuntimeException {

        BadCharacterException(char charAtI) {
            super("Bad character in base58 string, '" + charAtI + "'");
        }
    }

    private static final char[] DIGITS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final int[] VALUES = initValues(DIGITS);

    private final ByteBuffer byteBuffer;

    public Base58(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public Base58() {
        this(new ByteArrayByteBuffer());
    }

    private static final ThreadLocal<Base58> working = new ThreadLocal<>();

    /**
     * Encodes given bytes as a number in base58.
     * Threadsafe, uses an instance per thread.
     *
     * @param bytes bytes to encode
     * @return base58 string representation
     */
    public static CharSequence base58Encode(byte[] bytes) {
        return getThreadSharedBase58().encode(bytes);
    }

    /**
     * Decodes given bytes as a number in base58.
     * Threadsafe, uses an instance per thread.
     *
     * @param base58 string to decode
     * @return number as bytes
     */
    public static byte[] base58Decode(final CharSequence base58) {
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

    public interface EncodeTarget {
        void append(char c);
    }

    public interface DecodeWriter {
        void append(byte b);
    }

    public interface DecodeTarget {
        DecodeWriter getWriterForLength(int len);
    }

    public interface ByteBuffer {
        void grow(int atLeast);

        byte get(int index);

        void put(int index, byte value);

        void clear();
    }

    private ByteBuffer getBufferOfAtLeastBytes(final int atLeast) {
        byteBuffer.grow(atLeast);
        return byteBuffer;
    }

    /**
     * Encodes given bytes as a number in base58.
     *
     * @param bytes  bytes to encode
     * @param target where to write resulting string to
     */
    public void encode(final byte[] bytes, final EncodeTarget target) {
        final char[] a = DIGITS;
        final int bLen = bytes.length;
        final ByteBuffer d = getBufferOfAtLeastBytes(bLen << 1);
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
                    n = d.get(j);
                    n = (n << 8) + c;
                }
                d.put(j, (byte) (n % 58));
                c = n / 58;
                j++;
            }
        }
        while (j-- > 0) {
            target.append(a[d.get(j)]);
        }
        d.clear();
    }

    /**
     * Decodes given bytes as a number in base58.
     *
     * @param base58 string to decode
     * @return number as bytes
     */
    public byte[] decode(final CharSequence base58) {
        final ByteArrayTarget target = new ByteArrayTarget();
        decode(base58, target);
        return target.bytes;
    }

    /**
     * Decodes given bytes as a number in base58.
     *
     * @param base58 string to decode
     * @param target Receiver for output
     */
    public void decode(final CharSequence base58, final DecodeTarget target) {
        final int strLen = base58.length();
        final ByteBuffer d = getBufferOfAtLeastBytes(strLen);
        int dlen = -1;
        int blanks = 0;
        int j = 0;
        for (int i = 0; i < strLen; i++) {
            j = 0;
            final char charAtI = base58.charAt(i);
            int c = valueOf(charAtI);
            if (c < 0) {
                d.clear();
                throw new BadCharacterException(charAtI);
            }
            if (c == 0 && blanks == i) {
                blanks++;
            }
            while (j <= dlen || c != 0) {
                int n;
                if (j > dlen) {
                    dlen = j;
                    n = c;
                } else {
                    n = d.get(j) & 0xff;
                    n = n * 58 + c;
                }
                d.put(j, (byte) n);
                c = n >>> 8;
                j++;
            }
        }
        final int outputLength = j + blanks;
        final DecodeWriter writer = target.getWriterForLength(outputLength);
        for (int i = 0; i < blanks; i++) {
            writer.append((byte) 0);
        }
        final int end = outputLength - 1;
        for (int i = blanks; i < outputLength; i++) {
            writer.append(d.get(end - i));
        }
        d.clear();
    }

    private static int[] initValues(char[] alphabet) {
        final int[] lookup = new int['z' + 1];
        Arrays.fill(lookup, -1);
        for (int i = 0; i < alphabet.length; i++)
            lookup[alphabet[i]] = i;
        return lookup;
    }

    private static int valueOf(final char base58Char) {
        if (base58Char >= VALUES.length)
            return -1;
        return VALUES[base58Char];
    }
}