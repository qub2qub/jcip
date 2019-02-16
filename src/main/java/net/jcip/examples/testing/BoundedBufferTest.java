package net.jcip.examples.testing;

import junit.framework.TestCase;

/**
 * Basic unit tests for BoundedBuffer
 */
public class BoundedBufferTest extends TestCase {
    private static final long LOCKUP_DETECT_TIMEOUT = 1000;
    private static final int CAPACITY = 10000;
    private static final int THRESHOLD = 10000;

    void testIsEmptyWhenConstructed() {
        BoundedBufferSemaphore<Integer> bb = new BoundedBufferSemaphore<>(10);
        assertTrue(bb.isEmpty());
        assertFalse(bb.isFull());
    }

    void testIsFullAfterPuts() throws InterruptedException {
        BoundedBufferSemaphore<Integer> bb = new BoundedBufferSemaphore<>(10);
        for (int i = 0; i < 10; i++) {
            bb.put(i);
        }
        assertTrue(bb.isFull());
        assertFalse(bb.isEmpty());
    }

    /**
     * Testing blocking and responsiveness to interruption.
     */
    void testTakeBlocksWhenEmpty() {
        final BoundedBufferSemaphore<Integer> bb = new BoundedBufferSemaphore<>(10);
//        final BoundedBuffer<Integer> bb2 = new BoundedBuffer<Integer>(10);
        Thread taker = new Thread(() -> {
            try {
                int unused = bb.take();
                fail(); // if we get here, it's an error
            } catch (InterruptedException success) {
            }
        });
        try {
            taker.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT); // текущий поток заснёт
            taker.interrupt(); // прервать тестовый поток
            //The timed join ensures that the test completes even if .take() gets stuck in some unexpected way.
            taker.join(LOCKUP_DETECT_TIMEOUT); // присоединиться к тестовому потоку
            assertFalse(taker.isAlive()); // тестовый поток должен быть прерван
        } catch (Exception unexpected) {
            fail();
        }
    }

    class Big {
        double[] data = new double[100000];
    }

    void testLeak() throws InterruptedException {
        BoundedBufferSemaphore<Big> bb = new BoundedBufferSemaphore<>(CAPACITY);
        int heapSize1 = snapshotHeap();
        for (int i = 0; i < CAPACITY; i++) {
            bb.put(new Big());
        }
        for (int i = 0; i < CAPACITY; i++) {
            bb.take();
        }
        int heapSize2 = snapshotHeap();
        assertTrue(Math.abs(heapSize1 - heapSize2) < THRESHOLD);
    }

    private int snapshotHeap() {
        /* Snapshot heap and return heap size */
        return 0;
    }

}
