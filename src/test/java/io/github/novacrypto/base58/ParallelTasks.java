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

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ParallelTasks {

    public interface A {
        Runnable runTest();
    }

    private final Collection<A> tasks = new ArrayList<>();

    public void add(final A task) {
        tasks.add(task);
    }

    public void go() throws InterruptedException {
        final ExecutorService threads = Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors());
        try {
            final List<Runnable> results = Collections.synchronizedList(new LinkedList<Runnable>());
            final CountDownLatch latch = new CountDownLatch(tasks.size());
            for (final A task : tasks)
                threads.execute(() -> {
                    try {
                        results.add(task.runTest());
                    } finally {
                        latch.countDown();
                    }
                });
            latch.await();
            for (final Runnable assertion : results) {
                assertion.run();
            }
        } finally {
            threads.shutdown();
        }
    }
}