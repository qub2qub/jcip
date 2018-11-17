package net.jcip.examples.nonBlocking;

import net.jcip.annotations.*;
import net.jcip.examples.nonBlocking.SimulatedCAS;

/**
 * Nonblocking counter using CAS
 */
@ThreadSafe
public class CasCounter {
    private SimulatedCAS value;

    public int getValue() {
        return value.get();
    }

    public int increment() {
        int v;
        do {
            v = value.get();
        } while (v != value.compareAndSwap(v, v + 1));
        return v + 1;
    }
}
