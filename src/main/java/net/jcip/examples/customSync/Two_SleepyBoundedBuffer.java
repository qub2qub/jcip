package net.jcip.examples.customSync;

import net.jcip.annotations.*;

/**
 * Bounded buffer using crude blocking
 */
@ThreadSafe
public class Two_SleepyBoundedBuffer<V> extends BaseBoundedBuffer<V> {

    int SLEEP_GRANULARITY = 60;

    public Two_SleepyBoundedBuffer() {
        this(100);
    }

    public Two_SleepyBoundedBuffer(int size) {
        super(size);
    }

    public void put(V v) throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (!isFull()) {
                    doPut(v);
                    return;
                }
            }
            Thread.sleep(SLEEP_GRANULARITY);
        } // while
    }

    public V take() throws InterruptedException {
        while (true) {
            synchronized (this) {
                if (!isEmpty()) {
                    return doTake();
                }
            }
            Thread.sleep(SLEEP_GRANULARITY);
        } // while
    }
}
