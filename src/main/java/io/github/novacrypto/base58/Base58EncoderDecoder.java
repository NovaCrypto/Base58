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

package io.github.novacrypto.base58;

import java.util.Arrays;

final class Base58EncoderDecoder implements
        Encoder,
        Decoder,
        EncoderDecoder,
        SecureEncoder,
        SecureDecoder,
        SecureEncoderDecoder {

    private static final char[] DIGITS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final int[] VALUES = initValues(DIGITS);

    private final WorkingBuffer workingBuffer;

    Base58EncoderDecoder(WorkingBuffer workingBuffer) {
        this.workingBuffer = workingBuffer;
    }

    public String encode(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        encode(bytes, sb::append);
        return sb.toString();
    }

    private WorkingBuffer getBufferOfAtLeastBytes(final int atLeast) {
        workingBuffer.setCapacity(atLeast);
        return workingBuffer;
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
        final WorkingBuffer d = getBufferOfAtLeastBytes(bLen << 1);
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

    public byte[] decode(final CharSequence base58) {
        final ByteArrayTarget target = new ByteArrayTarget();
        decode(base58, target);
        return target.asByteArray();
    }

    public void decode(final CharSequence base58, final DecodeTarget target) {
        final int strLen = base58.length();
        final WorkingBuffer d = getBufferOfAtLeastBytes(strLen);
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