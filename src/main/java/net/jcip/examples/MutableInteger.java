package net.jcip.examples;

import net.jcip.annotations.*;

/**
 * Non-thread-safe mutable integer holder
 */
@NotThreadSafe
public class MutableInteger {
    private int value;

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;
    }
}








