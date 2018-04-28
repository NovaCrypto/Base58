/*
 *  Base58 library, a Java implementation of Base58 encode/decode
 *  Copyright (C) 2017-2018 Alan Evans, NovaCrypto
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
import io.github.novacrypto.base58.ByteArrayWorkingBuffer;
import io.github.novacrypto.base58.GeneralEncoderDecoder;
import io.github.novacrypto.base58.WorkingBuffer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public final class BufferClearTests {

    @Test
    public void clearsBufferAfterEncode() {
        final SpyWorkingBuffer spy = givenSpyBuffer();
        final GeneralEncoderDecoder encoder = Base58.newInstanceWithBuffer(spy);
        encoder.encode(new byte[]{(byte) 1});
        spy.assertClearedLast();
    }

    @Test
    public void clearsBufferAfterDecode() {
        final SpyWorkingBuffer spy = givenSpyBuffer();
        final GeneralEncoderDecoder encoder = Base58.newInstanceWithBuffer(spy);
        encoder.decode("a");
        spy.assertClearedLast();
    }

    @Test
    public void clearsBufferOnGetExceptionDuringEncode() {
        final SpyWorkingBuffer spy = givenSpyBuffer();
        final GeneralEncoderDecoder encoder = Base58.newInstanceWithBuffer(throwingGet(spy));
        assertThatThrownBy(() -> encoder.encode(new byte[]{(byte) 1}))
                .hasMessage("Can't get");
        spy.assertClearedLast();
    }

    @Test
    public void clearsBufferOnPutExceptionDuringEncode() {
        final SpyWorkingBuffer spy = givenSpyBuffer();
        final GeneralEncoderDecoder encoder = Base58.newInstanceWithBuffer(throwingPut(spy));
        assertThatThrownBy(() -> encoder.encode(new byte[]{(byte) 1}))
                .hasMessage("Can't put");
        spy.assertClearedLast();
    }

    @Test
    public void clearsBufferOnGetExceptionDuringDecode() {
        final SpyWorkingBuffer spy = givenSpyBuffer();
        final GeneralEncoderDecoder encoder = Base58.newInstanceWithBuffer(throwingGet(spy));
        assertThatThrownBy(() -> encoder.decode("a"))
                .hasMessage("Can't get");
        spy.assertClearedLast();
    }

    @Test
    public void clearsBufferOnPutExceptionDuringDecode() {
        final SpyWorkingBuffer spy = givenSpyBuffer();
        final GeneralEncoderDecoder encoder = Base58.newInstanceWithBuffer(throwingPut(spy));
        assertThatThrownBy(() -> encoder.decode("a"))
                .hasMessage("Can't put");
        spy.assertClearedLast();
    }

    @Test
    public void clearsBufferOnBadCharExceptionDuringDecode() {
        final SpyWorkingBuffer spy = givenSpyBuffer();
        final GeneralEncoderDecoder encoder = Base58.newInstanceWithBuffer(throwingGet(spy));
        assertThatThrownBy(() -> encoder.decode("0"))
                .isInstanceOf(BadCharacterException.class);
        spy.assertClearedLast();
    }

    @Test
    public void clearsBufferOnCharSequenceExceptionDuringDecode() {
        final SpyWorkingBuffer spy = givenSpyBuffer();
        final GeneralEncoderDecoder encoder = Base58.newInstanceWithBuffer(spy);
        assertThatThrownBy(() -> encoder.decode(throwingCharSequence()))
                .hasMessage("Bad CharSequence");
        spy.assertClearedLast();
    }

    private CharSequence throwingCharSequence() {
        return new CharSequence() {
            @Override
            public int length() {
                return 10;
            }

            @Override
            public char charAt(final int index) {
                if (index < 5)
                    return 'a';
                throw new RuntimeException("Bad CharSequence");
            }

            @Override
            public CharSequence subSequence(final int start, final int end) {
                return null;
            }
        };
    }

    private WorkingBuffer throwingGet(final WorkingBuffer buffer) {
        return new ThrowingGet(buffer);
    }

    private WorkingBuffer throwingPut(final WorkingBuffer buffer) {
        return new ThrowingPut(buffer);
    }

    private SpyWorkingBuffer givenSpyBuffer() {
        final WorkingBuffer buffer = new ByteArrayWorkingBuffer();
        return spy(buffer);
    }

    private SpyWorkingBuffer spy(final WorkingBuffer buffer) {
        return new SpyWorkingBuffer(buffer);
    }

    private static class SpyWorkingBuffer implements WorkingBuffer {
        private final WorkingBuffer buffer;
        private String lastMethod;

        SpyWorkingBuffer(final WorkingBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void setCapacity(final int atLeast) {
            lastMethod = "setCapacity";
            buffer.setCapacity(atLeast);
        }

        @Override
        public byte get(final int index) {
            lastMethod = "get";
            return buffer.get(index);
        }

        @Override
        public void put(final int index, final byte value) {
            lastMethod = "put";
            buffer.put(index, value);
        }

        @Override
        public void clear() {
            lastMethod = "clear";
            buffer.clear();
        }

        void assertClearedLast() {
            assertEquals("clear", lastMethod);
        }
    }

    private static class ThrowingGet extends SpyWorkingBuffer {
        ThrowingGet(final WorkingBuffer buffer) {
            super(buffer);
        }

        @Override
        public byte get(final int index) {
            super.get(index);
            throw new RuntimeException("Can't get");
        }
    }

    private static class ThrowingPut extends SpyWorkingBuffer {
        ThrowingPut(final WorkingBuffer buffer) {
            super(buffer);
        }

        @Override
        public void put(final int index, final byte value) {
            super.put(index, value);
            throw new RuntimeException("Can't put");
        }
    }
}
