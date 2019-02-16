package net.jcip.examples.testing;

import junit.framework.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Testing thread pool expansion (расширение, увеличение, разрастание пула потоков)
 */
public class TestThreadPool extends TestCase {

    private static final int MAX_SIZE = 10;
    private final TestingThreadFactory threadFactory = new TestingThreadFactory();

    public void testPoolExpansion() throws InterruptedException {

        ExecutorService exec = Executors.newFixedThreadPool(MAX_SIZE);
        ((ThreadPoolExecutor)exec).setThreadFactory(threadFactory);

        for (int i = 0; i < 10 * MAX_SIZE; i++) {
            exec.execute(() -> {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        for (int i = 0;
             i < 20 && threadFactory.numCreated.get() < MAX_SIZE;
             i++) {
            Thread.sleep(100);
        }
        assertEquals(MAX_SIZE, threadFactory.numCreated.get());
        exec.shutdownNow();
    }

}
