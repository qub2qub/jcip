package net.jcip.examples.nonBlocking;

import java.util.concurrent.locks.*;

import net.jcip.annotations.*;

/**
 * Random number generator using ReentrantLock
 */
@ThreadSafe
public class ReentrantLockPseudoRandom extends PseudoRandom {
    private final Lock lock = new ReentrantLock(false);
    private int seed; // shared seed state

    ReentrantLockPseudoRandom(int seed) {
        this.seed = seed;
    }

    public int nextInt(int n) {
        lock.lock();
        try {
            int s = seed;
            // обновление сид --- это shared операция
            // вычисление нового инта -- это thread-local операция.
            seed = calculateNext(s);
            int remainder = s % n;
            return remainder > 0 ? remainder : remainder + n;
        } finally {
            lock.unlock();
        }
    }
}
