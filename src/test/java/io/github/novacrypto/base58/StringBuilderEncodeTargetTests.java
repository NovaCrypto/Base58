/*
 *  Base58 library, a Java implementation of Base58 encode/decode
 *  Copyright (C) 2017-2019 Alan Evans, NovaCrypto
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

import static org.junit.Assert.assertEquals;

public final class StringBuilderEncodeTargetTests {

    @Test
    public void append_adds_to_the_string() {
        final StringBuilderEncodeTarget target = new StringBuilderEncodeTarget();
        target.append('a');
        assertEquals("a", target.toString());
    }

    @Test
    public void append_two_chars_to_the_string() {
        final StringBuilderEncodeTarget target = new StringBuilderEncodeTarget();
        target.append('a');
        target.append('b');
        assertEquals("ab", target.toString());
    }

    @Test
    public void clear_clears_the_buffer() {
        final StringBuilderEncodeTarget target = new StringBuilderEncodeTarget();
        target.append('a');
        target.clear();
        assertEquals("", target.toString());
    }

    @Test
    public void clear_of_string_of_two_clears_the_buffer() {
        final StringBuilderEncodeTarget target = new StringBuilderEncodeTarget();
        target.append('a');
        target.append('b');
        target.clear();
        assertEquals("", target.toString());
    }
}