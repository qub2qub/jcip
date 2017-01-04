package net.jcip.examples;

import java.util.concurrent.*;

/**
 * TestHarness
 * <p/>
 * Using CountDownLatch for starting and stopping threads in timing tests
 *
 * @author Brian Goetz and Tim Peierls
 */
public class TestHarness {
    public long timeTasks(int nThreads, final Runnable task)
            throws InterruptedException {
        // для этого Latch мы вручную вызовем countDown()
        final CountDownLatch startGate = new CountDownLatch(1);
        // для этого Latch каждый поток в конце выполнения будет делать -1 у counter
        final CountDownLatch endGate = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        // при .await() текущий поток засыпает и ждёт пока startGate counter достигнет 0
                        // после этого начнётся дальнейшее выполнение
                        startGate.await();
                        try {
                            task.run();
                        } finally {
                            endGate.countDown();
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            };
            t.start();
        }

        long start = System.nanoTime();
        startGate.countDown(); // открываем первые порота
        endGate.await(); // текущий поток блокируется, пока все потоки не сделают endGate.countDown()
        // после завершения всех поток counter достигнет 0 и будет замерено время их выполнения.
        long end = System.nanoTime();
        return end - start;
    }
}
