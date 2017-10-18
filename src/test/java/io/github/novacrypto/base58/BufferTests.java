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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public final class BufferTests {

    interface ByteBufferFactory {
        ByteBuffer create();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {(ByteBufferFactory) ByteArrayByteBuffer::new},
                {(ByteBufferFactory) SecureByteBuffer::new}
        });
    }

    private final ByteBuffer buffer;

    public BufferTests(ByteBufferFactory factory) {
        buffer = factory.create();
    }

    @Test
    public void can_put_and_get() {
        buffer.setCapacity(1);
        buffer.put(0, (byte) 1);
        assertEquals((byte) 1, buffer.get(0));
    }

    @Test
    public void can_put_and_get_value_2() {
        buffer.setCapacity(1);
        buffer.put(0, (byte) 2);
        assertEquals((byte) 2, buffer.get(0));
    }

    @Test
    public void can_put_and_get_second_index() {
        buffer.setCapacity(2);
        buffer.put(1, (byte) 3);
        assertEquals((byte) 3, buffer.get(1));
    }

    @Test
    public void cant_put_negative() {
        assertThatThrownBy(() ->
                buffer.put(-1, (byte) 2))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void cant_get_negative() {
        assertThatThrownBy(() ->
                buffer.get(-1))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void cant_put_if_have_not_set_capacity() {
        assertThatThrownBy(() ->
                buffer.put(0, (byte) 2))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void cant_get_if_have_not_set_capacity() {
        assertThatThrownBy(() ->
                buffer.get(0))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void cant_put_if_have_not_set_capacity_high_enough() {
        buffer.setCapacity(1);
        assertThatThrownBy(() ->
                buffer.put(1, (byte) 0))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void cant_get_if_have_not_set_capacity_high_enough() {
        buffer.setCapacity(1);
        assertThatThrownBy(() ->
                buffer.get(1))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void initial_values() {
        buffer.setCapacity(10);
        assertAllCleared(10);
    }

    @Test
    public void clear() {
        buffer.setCapacity(10);
        for (int i = 0; i < 10; i++) {
            buffer.put(i, (byte) 1);
        }
        buffer.clear();
        assertAllCleared(10);
    }

    @Test
    public void resize_same_size() {
        buffer.setCapacity(10);
        for (int i = 0; i < 10; i++) {
            buffer.put(i, (byte) 1);
        }
        buffer.setCapacity(10);
        assertAllCleared(10);
    }

    @Test
    public void resize_new_size() {
        buffer.setCapacity(10);
        for (int i = 0; i < 10; i++) {
            buffer.put(i, (byte) 1);
        }
        buffer.setCapacity(20);
        assertAllCleared(20);
    }

    private void assertAllCleared(int len) {
        for (int i = 0; i < len; i++) {
            assertEquals((byte) 255, buffer.get(i));
        }
    }
}