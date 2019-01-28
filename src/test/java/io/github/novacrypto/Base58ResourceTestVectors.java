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

package io.github.novacrypto;

import io.github.novacrypto.base58.json.TestVector;
import io.github.novacrypto.base58.json.TestVectorCollection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public final class Base58ResourceTestVectors {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"LargeCase.json"},
                {"OneByte.json"},
                {"Random82Length.json"},
                {"RandomDecreasingLength.json"},
                {"RandomIncreasingLength.json"}
        });
    }

    private final TestVectorCollection collection;

    public Base58ResourceTestVectors(final String resourceName) {
        collection = Resources.loadJsonResource(resourceName, TestVectorCollection.class);
    }

    @Test
    public void encodeStatic() {
        for (final TestVector vector : collection.vectors) {
            final byte[] bytes = fromHex(vector.dataHex);
            Assert.assertEquals(vector.dataBase58, Base58EncodeTests.base58StaticEncode(bytes));
        }
    }

    @Test
    public void encodeInstance() {
        for (final TestVector vector : collection.vectors) {
            final byte[] bytes = fromHex(vector.dataHex);
            Assert.assertEquals(vector.dataBase58, Base58EncodeTests.base58InstanceEncode(bytes));
        }
    }

    @Test
    public void encodeSecureInstance() {
        for (final TestVector vector : collection.vectors) {
            final byte[] bytes = fromHex(vector.dataHex);
            Assert.assertEquals(vector.dataBase58, Base58EncodeTests.base58SecureInstanceEncode(bytes));
        }
    }

    @Test
    public void encodeParallelStatic() throws InterruptedException {
        final ParallelTasks parallelTasks = new ParallelTasks();
        for (final TestVector vector : collection.vectors) {
            parallelTasks.add(() -> {
                final String actual = Base58EncodeTests.base58StaticEncode(fromHex(vector.dataHex));
                return () -> assertEquals(vector.dataBase58, actual);
            });
        }
        parallelTasks.go();
    }

    @Test
    public void encodeParallelInstance() throws InterruptedException {
        final ParallelTasks parallelTasks = new ParallelTasks();
        for (final TestVector vector : collection.vectors) {
            parallelTasks.add(() -> {
                final String actual = Base58EncodeTests.base58InstanceEncode(fromHex(vector.dataHex));
                return () -> assertEquals(vector.dataBase58, actual);
            });
        }
        parallelTasks.go();
    }

    @Test
    public void encodeParallelSecureInstance() throws InterruptedException {
        final ParallelTasks parallelTasks = new ParallelTasks();
        for (final TestVector vector : collection.vectors) {
            parallelTasks.add(() -> {
                final String actual = Base58EncodeTests.base58SecureInstanceEncode(fromHex(vector.dataHex));
                return () -> assertEquals(vector.dataBase58, actual);
            });
        }
        parallelTasks.go();
    }

    @Test
    public void decodeStatic() {
        for (final TestVector vector : collection.vectors) {
            final byte[] bytes = fromHex(vector.dataHex);
            Assert.assertArrayEquals(bytes, Base58DecodeTests.base58StaticDecode(vector.dataBase58));
        }
    }

    @Test
    public void decodeInstance() {
        for (final TestVector vector : collection.vectors) {
            final byte[] bytes = fromHex(vector.dataHex);
            Assert.assertArrayEquals(bytes, Base58DecodeTests.base58InstanceDecode(vector.dataBase58));
        }
    }

    @Test
    public void decodeSecureInstance() {
        for (final TestVector vector : collection.vectors) {
            final byte[] bytes = fromHex(vector.dataHex);
            Assert.assertArrayEquals(bytes, Base58DecodeTests.base58SecureInstanceDecode(vector.dataBase58));
        }
    }

    @Test
    public void decodeParallelInstance() throws InterruptedException {
        final ParallelTasks parallelTasks = new ParallelTasks();
        for (final TestVector vector : collection.vectors) {
            parallelTasks.add(() -> {
                final byte[] expected = fromHex(vector.dataHex);
                final byte[] actual = Base58DecodeTests.base58InstanceDecode(vector.dataBase58);
                return () -> assertArrayEquals(expected, actual);
            });
        }
        parallelTasks.go();
    }

    @Test
    public void decodeParallelStatic() throws InterruptedException {
        final ParallelTasks parallelTasks = new ParallelTasks();
        for (final TestVector vector : collection.vectors) {
            parallelTasks.add(() -> {
                final byte[] expected = fromHex(vector.dataHex);
                final byte[] actual = Base58DecodeTests.base58StaticDecode(vector.dataBase58);
                return () -> assertArrayEquals(expected, actual);
            });
        }
        parallelTasks.go();
    }

    @Test
    public void decodeParallelSecureInstance() throws InterruptedException {
        final ParallelTasks parallelTasks = new ParallelTasks();
        for (final TestVector vector : collection.vectors) {
            parallelTasks.add(() -> {
                final byte[] expected = fromHex(vector.dataHex);
                final byte[] actual = Base58DecodeTests.base58SecureInstanceDecode(vector.dataBase58);
                return () -> assertArrayEquals(expected, actual);
            });
        }
        parallelTasks.go();
    }

    private static byte[] fromHex(final String s) {
        final int len = s.length();
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) ((
                    Character.digit(s.charAt(i), 16) << 4) +
                    Character.digit(s.charAt(i + 1), 16)
            );
        return data;
    }
}