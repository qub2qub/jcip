package net.jcip.examples.vehicleTracker;

import java.util.*;
import java.util.concurrent.*;

import net.jcip.annotations.*;

/**
 * PublishingVehicleTracker
 * <p/>
 * Vehicle tracker that safely publishes underlying state
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class PublishingVehicleTracker {
    private final Map<String, SafeMutablePoint> locations;
    private final Map<String, SafeMutablePoint> unmodifiableMap;

    public PublishingVehicleTracker(Map<String, SafeMutablePoint> locations) {
        this.locations = new ConcurrentHashMap<String, SafeMutablePoint>(locations);
        this.unmodifiableMap = Collections.unmodifiableMap(this.locations);
    }

    public Map<String, SafeMutablePoint> getLocations() {
        return unmodifiableMap;
    }

    public SafeMutablePoint getLocation(String id) {
        return locations.get(id);
    }

    public void setLocation(String id, int x, int y) {
        if (!locations.containsKey(id))
            throw new IllegalArgumentException("invalid vehicle name: " + id);
        locations.get(id).set(x, y);
    }
}
