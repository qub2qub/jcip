package net.jcip.examples.vehicleTracker;

import net.jcip.annotations.*;

/**
 * ImmutablePoint
 * <p/>
 * Immutable ImmutablePoint class used by DelegatingVehicleTracker
 *
 * @author Brian Goetz and Tim Peierls
 */
@Immutable
public class ImmutablePoint {
    public final int x, y;

    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
