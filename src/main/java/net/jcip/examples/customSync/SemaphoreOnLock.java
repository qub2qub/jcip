package net.jcip.examples.customSync;

import java.util.concurrent.locks.*;

import net.jcip.annotations.*;

/**
 * Counting semaphore implemented using Lock
 * (Not really how java.util.concurrent.Semaphore is implemented)
 */
@ThreadSafe
public class SemaphoreOnLock {
    @GuardedBy("lock") private int permits;
    private final Lock lock = new ReentrantLock();
    // CONDITION PREDICATE: permitsAvailable (permits > 0)
    private final Condition permitsAvailable = lock.newCondition();

    SemaphoreOnLock(int initialPermits) {
        lock.lock();
        try {
            permits = initialPermits;
        } finally {
            lock.unlock();
        }
    }

    // BLOCKS-UNTIL: permitsAvailable
    public void acquire() throws InterruptedException {
        lock.lock(); // 1е место = one point where they might block
        try {
            while (permits <= 0) {
                permitsAvailable.await(); // 2е место = 2nd point where they might block
            }
            --permits;
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        lock.lock();
        try {
            ++permits;
            permitsAvailable.signal();
        } finally {
            lock.unlock();
        }
    }
}
