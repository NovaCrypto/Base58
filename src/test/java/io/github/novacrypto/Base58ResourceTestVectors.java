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

import static io.github.novacrypto.Base58Tests.base58Instance;
import static io.github.novacrypto.Base58Tests.base58Static;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public final class Base58ResourceTestVectors {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
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
    public void encode() {
        for (TestVector vector : collection.vectors) {
            byte[] bytes = fromHex(vector.dataHex);
            assertEquals(vector.dataBase58, base58Instance(bytes));
            assertEquals(vector.dataBase58, base58Static(bytes));
        }
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