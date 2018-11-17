package net.jcip.examples.customSync;

import java.util.concurrent.locks.*;

import net.jcip.annotations.*;

/**
 * Bounded buffer using explicit condition variables
 */

@ThreadSafe
public class ConditionBoundedBuffer <T> {

    protected final Lock lock = new ReentrantLock();

    // CONDITION PREDICATE: notFull (count < items.length)
    private final Condition notFull = lock.newCondition();

    // CONDITION PREDICATE: notEmpty (count > 0)
    private final Condition notEmpty = lock.newCondition();

    private static final int BUFFER_SIZE = 100;
    @GuardedBy("lock") private final T[] items = (T[]) new Object[BUFFER_SIZE];
    @GuardedBy("lock") private int tail, head, count;

    // BLOCKS-UNTIL: notFull
    public void put(T x) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) {
                // блокируется если очередь полная и
                // ждёт сигнала от notFull
                notFull.await();
                // т.е. есть отдельный wait set, в котором потоки ждут сигнала, что очередь не полная
            }
            items[tail] = x;
            if (++tail == items.length) {
                tail = 0;
            }
            ++count;
            // тут он сигнализирует, что очередь не пустая, и кто-то может забрать из неё
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    // BLOCKS-UNTIL: notEmpty
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                // блокируется если очередь пустая и
                // ждёт сигнала от notEmpty
                notEmpty.await();
                // т.е. есть отдельный wait set, в котором потоки ждут сигнала, что очередь не пустая
            }
            T x = items[head];
            items[head] = null;
            if (++head == items.length) {
                head = 0;
            }
            --count;
            // тут он сигнализирует, что очередь не полная, и кто-то может положить в неё
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }
}
