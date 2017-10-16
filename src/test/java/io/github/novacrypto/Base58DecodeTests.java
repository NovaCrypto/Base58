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

import org.junit.Test;

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
        byte[] bytes = new byte[256];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }
        assertBase58(
                bytes,
                "1cWB5HCBdLjAuqGGReWE3R3CguuwSjw6RHn39s2yuDRTS5NsBgNiFpWgAnEx6VQi8csexkgYw3mdYrMHr8x9i7aEwP8kZ7vccXWqKDvGv3u1GxFKPuAkn8JCPPGDMf3vMMnbzm6Nh9zh1gcNsMvH3ZNLmP5fSG6DGbbi2tuwMWPthr4boWwCxf7ewSgNQeacyozhKDDQQ1qL5fQFUW52QKUZDZ5fw3KXNQJMcNTcaB723LchjeKun7MuGW5qyCBZYzA1KjofN1gYBV3NqyhQJ3Ns746GNuf9N2pQPmHz4xpnSrrfCvy6TVVz5d4PdrjeshsWQwpZsZGzvbdAdN8MKV5QsBDY"
        );
    }

    @Test
    public void badCharacterZero() {
        assertThatThrownBy(
                () -> base58InstanceDecode("0"))
                .isInstanceOf(Base58.BadCharacterException.class)
                .hasMessage("Bad character in base58 string, '0'");
    }

    @Test
    public void badCharacterLowerL() {
        assertThatThrownBy(
                () -> base58InstanceDecode("l"))
                .isInstanceOf(Base58.BadCharacterException.class)
                .hasMessage("Bad character in base58 string, 'l'");
    }

    @Test
    public void badCharacterHighUnicode() {
        assertThatThrownBy(
                () -> base58InstanceDecode("\u01ff"))
                .isInstanceOf(Base58.BadCharacterException.class)
                .hasMessage("Bad character in base58 string, '\u01ff'");
    }

    @Test
    public void badCharacterJustAboveZ() {
        final char next = 'z' + 1;
        assertThatThrownBy(
                () -> base58InstanceDecode("" + next))
                .isInstanceOf(Base58.BadCharacterException.class)
                .hasMessage("Bad character in base58 string, '{'");
    }

    private void assertBase58(byte[] expected, String input) {
        assertArrayEquals(expected, base58InstanceDecode(input));
        assertArrayEquals(expected, base58StaticDecode(input));
    }

    static byte[] base58InstanceDecode(CharSequence base58) {
        return new Base58().decode(base58);
    }

    static byte[] base58StaticDecode(CharSequence base58) {
        return Base58.base58Decode(base58);
    }
}