/*
 *  Base58 library, a Java implementation of Base58 encode/decode
 *
 *  Copyright (C) 2017-2022 Alan Evans, NovaCrypto
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

import io.github.novacrypto.base58.BadCharacterException;
import io.github.novacrypto.base58.Base58;
import org.junit.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertArrayEquals;

public final class Base58DecodeTests {

    @Test
    public void emptyByteArray() {
        assertBase58(new byte[0], "");
    }

    @Test
    public void zeroByteArrayLength1() {
        assertBase58(new byte[1], "1");
    }

    @Test
    public void zeroByteArrayLength2() {
        assertBase58(new byte[2], "11");
    }

    @Test
    public void zeroSecond() {
        assertBase58(new byte[]{1, 0}, "5R");
    }

    @Test
    public void twoFiveFiveByteArrayLength1() {
        assertBase58(new byte[]{(byte) 255}, "5Q");
    }

    @Test
    public void twoFiveFiveByteArrayLength2() {
        assertBase58(new byte[]{(byte) 255, (byte) 255}, "LUv");
    }

    @Test
    public void allByteValues() {
        final byte[] bytes = new byte[256];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }
        assertBase58(
                bytes,
                "1cWB5HCBdLjAuqGGReWE3R3CguuwSjw6RHn39s2yuDRTS5NsBgNiFpWgAnEx6VQi8csexkgYw3mdYrMHr8x9i7aEwP8kZ7vccXWqKDvGv3u1GxFKPuAkn8JCPPGDMf3vMMnbzm6Nh9zh1gcNsMvH3ZNLmP5fSG6DGbbi2tuwMWPthr4boWwCxf7ewSgNQeacyozhKDDQQ1qL5fQFUW52QKUZDZ5fw3KXNQJMcNTcaB723LchjeKun7MuGW5qyCBZYzA1KjofN1gYBV3NqyhQJ3Ns746GNuf9N2pQPmHz4xpnSrrfCvy6TVVz5d4PdrjeshsWQwpZsZGzvbdAdN8MKV5QsBDY"
        );
    }

    private static class KnownBytes {

        private final Random random = new Random(1234);

        byte[] getBytes() {
            final byte[] bytes = new byte[2048];
            random.nextBytes(bytes);
            return bytes;
        }
    }

    @Test
    public void largeCase() {
        final byte[] bytes = new KnownBytes().getBytes();
        assertBase58(bytes, "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");
    }

    @Test
    public void badCharacterZero() {
        assertThatThrownBy(
                () -> base58InstanceDecode("0"))
                .isInstanceOf(BadCharacterException.class)
                .hasMessage("Bad character in base58 string, '0'");
    }

    @Test
    public void badCharacterLowerL() {
        assertThatThrownBy(
                () -> base58InstanceDecode("l"))
                .isInstanceOf(BadCharacterException.class)
                .hasMessage("Bad character in base58 string, 'l'");
    }

    @Test
    public void badCharacterHighUnicode() {
        assertThatThrownBy(
                () -> base58InstanceDecode("\u01ff"))
                .isInstanceOf(BadCharacterException.class)
                .hasMessage("Bad character in base58 string, '\u01ff'");
    }

    @Test
    public void badCharacterJustAboveZ() {
        final char next = 'z' + 1;
        assertThatThrownBy(
                () -> base58InstanceDecode("" + next))
                .isInstanceOf(BadCharacterException.class)
                .hasMessage("Bad character in base58 string, '{'");
    }

    private void assertBase58(final byte[] expected, final String input) {
        assertArrayEquals(expected, base58InstanceDecode(input));
        assertArrayEquals(expected, base58StaticDecode(input));
        assertArrayEquals(expected, base58SecureInstanceDecode(input));
    }

    static byte[] base58InstanceDecode(final CharSequence base58) {
        return Base58.newInstance().decode(base58);
    }

    static byte[] base58SecureInstanceDecode(final CharSequence base58) {
        final InsecureByteArrayTarget target = new InsecureByteArrayTarget();
        Base58.newSecureInstance().decode(base58, target);
        return target.asByteArray();
    }

    static byte[] base58StaticDecode(final CharSequence base58) {
        return Base58.base58Decode(base58);
    }
}