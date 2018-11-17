package net.jcip.examples.testing;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Testing thread pool expansion
 */
public class TestThreadPool extends TestCase {

    private final TestingThreadFactory threadFactory = new TestingThreadFactory();

    @Test(expected = RuntimeException.class)
    public void raz() {
        System.out.println("dddd");
        throw new RuntimeException("aaaaa");
    }

    public void testPoolExpansion() throws InterruptedException {
        int MAX_SIZE = 10;
        ExecutorService exec = Executors.newFixedThreadPool(MAX_SIZE);
        ((ThreadPoolExecutor)exec).setThreadFactory(threadFactory);

        for (int i = 0; i < 10 * MAX_SIZE; i++)
            exec.execute(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        for (int i = 0;
             i < 20 && threadFactory.numCreated.get() < MAX_SIZE;
             i++) {
            Thread.sleep(100);
        }
        assertEquals(MAX_SIZE, threadFactory.numCreated.get());
        exec.shutdownNow();
    }
}

class TestingThreadFactory implements ThreadFactory {
    public final AtomicInteger numCreated = new AtomicInteger();
    private final ThreadFactory factory = Executors.defaultThreadFactory();

    public Thread newThread(Runnable r) {
        numCreated.incrementAndGet();
        return factory.newThread(r);
    }
}
