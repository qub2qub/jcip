package net.jcip.examples.nonBlocking;

import java.util.concurrent.atomic.*;

import net.jcip.annotations.*;

/**
 * Preserving multivariable invariants using CAS
 */
@ThreadSafe
public class CasNumberRange {

    private final AtomicReference<IntPair> values = new AtomicReference<IntPair>(new IntPair(0, 0));
    public int getLower() {
        return values.get().lower;
    }
    public int getUpper() {
        return values.get().upper;
    }

    public void setLower(int newLower) {
        while (true) {
            IntPair oldv = values.get();
            if (newLower > oldv.upper) {
                throw new IllegalArgumentException("Can't set lower to " + newLower + " > upper("+oldv.upper+")");
            }
            IntPair newv = new IntPair(newLower, oldv.upper);
            if (values.compareAndSet(oldv, newv)) {
                return;
            }
        }
    }

    public void setUpper(int newUpper) {
        while (true) {
            IntPair oldv = values.get();
            if (newUpper < oldv.lower) {
                throw new IllegalArgumentException("Can't set upper to " + newUpper + " < lower(" + oldv.lower + ")");
            }
            IntPair newv = new IntPair(oldv.lower, newUpper);
            if (values.compareAndSet(oldv, newv)) {
                return;
            }
        }
    }


    @Immutable
    private static class IntPair {
        // INVARIANT: lower <= upper
        final int lower;
        final int upper;

        public IntPair(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }
}
