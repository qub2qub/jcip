package net.jcip.examples.vehicleTracker;

import net.jcip.annotations.*;

/**
 * Mutable ImmutablePoint class similar to java.awt.ImmutablePoint
 */
@NotThreadSafe
public class MutablePoint {
    public int x, y;

    public MutablePoint() {
        x = 0;
        y = 0;
    }

    public MutablePoint(MutablePoint p) {
        this.x = p.x;
        this.y = p.y;
    }
}
