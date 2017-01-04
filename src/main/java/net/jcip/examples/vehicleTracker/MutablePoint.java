package net.jcip.examples.vehicleTracker;

import net.jcip.annotations.*;

/**
 * MutablePoint
 * <p/>
 * Mutable ImmutablePoint class similar to java.awt.ImmutablePoint
 *
 * @author Brian Goetz and Tim Peierls
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
