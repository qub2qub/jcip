package net.jcip.examples.vehicleTracker;

import net.jcip.annotations.*;

@ThreadSafe
public class SafeMutablePoint {
    @GuardedBy("this") private int x, y;

    private SafeMutablePoint(int[] a) {
        // PRIVATE
        this(a[0], a[1]);
    }

    public SafeMutablePoint(SafeMutablePoint p) {
        this(p.get());
    }

    public SafeMutablePoint(int x, int y) {
        this.set(x, y);
    }

    public synchronized int[] get() {
        return new int[]{x, y};
    }

    public synchronized void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
