package net.jcip.examples.vehicleTracker;

import net.jcip.annotations.*;

/**
 * Immutable ImmutablePoint class used by DelegatingVehicleTracker
 */
@Immutable
public class ImmutablePoint {
    public final int x, y;

    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
