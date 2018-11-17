package net.jcip.examples;

import net.jcip.annotations.*;

/**
 * Thread-safe mutable integer holder
 */
@ThreadSafe
public class SynchronizedInteger {
    @GuardedBy("this") private int value;

    public synchronized int get() {
        return value;
    }

    public synchronized void set(int value) {
        this.value = value;
    }
}
