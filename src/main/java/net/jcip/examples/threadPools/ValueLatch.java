package net.jcip.examples.threadPools;

import java.util.concurrent.*;

import net.jcip.annotations.*;

/**
 * ValueLatch
 * <p/>
 * Result-bearing latch used by ConcurrentPuzzleSolver
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class ValueLatch <T> {

    @GuardedBy("this") private T value = null;
    private final CountDownLatch done = new CountDownLatch(1);

    public boolean isSet() {
        return (done.getCount() == 0);
    }

    public synchronized void setValue(T newValue) {
        if (!isSet()) {
            value = newValue;
            done.countDown();
        }
    }

    public T getValue() throws InterruptedException {
        done.await();
        // чтобы setValue смог закончиться и записать обновлённое занчение из другого потока
        synchronized (this) {
            return value;
        }
    }
}
