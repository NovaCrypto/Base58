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

public final class Base58 {

    public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private byte[] bytes;

    private static final ThreadLocal<Base58> working = new ThreadLocal<Base58>();

    public static String encodeStatic(byte[] B) {
        return getThreadSharedBase58().encode(B);
    }

    private static Base58 getThreadSharedBase58() {
        Base58 base58 = working.get();
        if (base58 == null) {
            base58 = new Base58();
            working.set(base58);
        }
        return base58;
    }

    public String encode(byte[] B) {
        final StringBuilder sb = new StringBuilder();
        encode(B, new Target() {
            public void append(char c) {
                sb.append(c);
            }
        });
        return sb.toString();
    }

    public interface Target {
        void append(char x);
    }

    private byte[] getBufferOfAtLeastBytes(int atLeast) {
        if (bytes == null || bytes.length < atLeast) {
            if (bytes != null)
                Arrays.fill(bytes, (byte) 255);
            bytes = new byte[atLeast];
        }
        Arrays.fill(bytes, (byte) 255);
        return bytes;
    }

    public void encode(byte[] B, Target target) {
        final char[] A = ALPHABET;
        final int bLen = B.length;
        final byte[] d = getBufferOfAtLeastBytes(bLen << 1);
        int dlen = -1;
        int blanks = 0;
        int j = 0;
        for (int i = 0; i < bLen; i++) {
            int c = B[i] & 0xff;
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