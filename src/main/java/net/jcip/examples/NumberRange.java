package net.jcip.examples;

import java.util.concurrent.atomic.*;

/**
 * Number range class that does not sufficiently protect its invariants
 */
public class NumberRange {
    // INVARIANT: lower <= upper
    // an invariant constrains the two numbers and they cannot be updated simultaneously
    // while preserving the invariant, a number range class using volatile references or multiple atomic integers
    // will have unsafe check-then-act sequences.
    private final AtomicInteger lower = new AtomicInteger(0);
    private final AtomicInteger upper = new AtomicInteger(0);

    /**
     * будут race conditions когда одновременно будет изменяться lower и upper
     */
    public void setLower(int i) {
        // Warning -- unsafe check-then-act
        if (i > upper.get()) {
            throw new IllegalArgumentException("can't set lower to " + i + " > upper");
        }
        lower.set(i);
    }

    public void setUpper(int i) {
        // Warning -- unsafe check-then-act
        if (i < lower.get()) {
            throw new IllegalArgumentException("can't set upper to " + i + " < lower");
        }
        upper.set(i);
    }

    public boolean isInRange(int i) {
        return (i >= lower.get() && i <= upper.get());
    }
}

