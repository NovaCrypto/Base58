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

import io.github.novacrypto.json.TestVector;
import io.github.novacrypto.json.TestVectorCollection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static io.github.novacrypto.Base58DecodeTests.*;
import static io.github.novacrypto.Base58EncodeTests.*;
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

    public Base58ResourceTestVectors(String resourceName) {
        collection = Resources.loadJsonResource(resourceName, TestVectorCollection.class);
    }

    @Test
    public void encodeStatic() {
        for (TestVector vector : collection.vectors) {
            byte[] bytes = fromHex(vector.dataHex);
            assertEquals(vector.dataBase58, base58StaticEncode(bytes));
        }
    }

    @Test
    public void encodeInstance() {
        for (TestVector vector : collection.vectors) {
            byte[] bytes = fromHex(vector.dataHex);
            assertEquals(vector.dataBase58, base58InstanceEncode(bytes));
        }
    }

    @Test
    public void encodeSecureInstance() {
        for (TestVector vector : collection.vectors) {
            byte[] bytes = fromHex(vector.dataHex);
            assertEquals(vector.dataBase58, base58SecureInstanceEncode(bytes));
        }
    }

    @Test
    public void encodeParallelStatic() throws InterruptedException {
        ParallelTasks parallelTasks = new ParallelTasks();
        for (TestVector vector : collection.vectors) {
            parallelTasks.add(() -> {
                String actual = base58StaticEncode(fromHex(vector.dataHex));
                return () -> assertEquals(vector.dataBase58, actual);
            });
        }
        parallelTasks.go();
    }

    @Test
    public void encodeParallelInstance() throws InterruptedException {
        ParallelTasks parallelTasks = new ParallelTasks();
        for (TestVector vector : collection.vectors) {
            parallelTasks.add(() -> {
                String actual = base58InstanceEncode(fromHex(vector.dataHex));
                return () -> assertEquals(vector.dataBase58, actual);
            });
        }
        parallelTasks.go();
    }

    @Test
    public void encodeParallelSecureInstance() throws InterruptedException {
        ParallelTasks parallelTasks = new ParallelTasks();
        for (TestVector vector : collection.vectors) {
            parallelTasks.add(() -> {
                String actual = base58SecureInstanceEncode(fromHex(vector.dataHex));
                return () -> assertEquals(vector.dataBase58, actual);
            });
        }
        parallelTasks.go();
    }

    @Test
    public void decodeStatic() {
        for (TestVector vector : collection.vectors) {
            byte[] bytes = fromHex(vector.dataHex);
            assertArrayEquals(bytes, base58StaticDecode(vector.dataBase58));
        }
    }

    @Test
    public void decodeInstance() {
        for (TestVector vector : collection.vectors) {
            byte[] bytes = fromHex(vector.dataHex);
            assertArrayEquals(bytes, base58InstanceDecode(vector.dataBase58));
        }
    }

    @Test
    public void decodeSecureInstance() {
        for (TestVector vector : collection.vectors) {
            byte[] bytes = fromHex(vector.dataHex);
            assertArrayEquals(bytes, base58SecureInstanceDecode(vector.dataBase58));
        }
    }

    @Test
    public void decodeParallelInstance() throws InterruptedException {
        ParallelTasks parallelTasks = new ParallelTasks();
        for (TestVector vector : collection.vectors) {
            parallelTasks.add(() -> {
                byte[] expected = fromHex(vector.dataHex);
                byte[] actual = base58InstanceDecode(vector.dataBase58);
                return () -> assertArrayEquals(expected, actual);
            });
        }
        parallelTasks.go();
    }

    @Test
    public void decodeParallelStatic() throws InterruptedException {
        ParallelTasks parallelTasks = new ParallelTasks();
        for (TestVector vector : collection.vectors) {
            parallelTasks.add(() -> {
                byte[] expected = fromHex(vector.dataHex);
                byte[] actual = base58StaticDecode(vector.dataBase58);
                return () -> assertArrayEquals(expected, actual);
            });
        }
        parallelTasks.go();
    }

    @Test
    public void decodeParallelSecureInstance() throws InterruptedException {
        ParallelTasks parallelTasks = new ParallelTasks();
        for (TestVector vector : collection.vectors) {
            parallelTasks.add(() -> {
                byte[] expected = fromHex(vector.dataHex);
                byte[] actual = base58SecureInstanceDecode(vector.dataBase58);
                return () -> assertArrayEquals(expected, actual);
            });
        }
        parallelTasks.go();
    }

    private static byte[] fromHex(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) ((
                    Character.digit(s.charAt(i), 16) << 4) +
                    Character.digit(s.charAt(i + 1), 16)
            );
        return data;
    }
}