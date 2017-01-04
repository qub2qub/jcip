package net.jcip.examples.vehicleTracker;

import java.util.*;
import java.util.concurrent.*;

import net.jcip.annotations.*;

/**
 * DelegatingVehicleTracker
 * <p/>
 * Delegating thread safety to a ConcurrentHashMap
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class DelegatingVehicleTracker {
    private final ConcurrentMap<String, ImmutablePoint> locations;
    private final Map<String, ImmutablePoint> unmodifiableMap;

    public DelegatingVehicleTracker(Map<String, ImmutablePoint> points) {
        locations = new ConcurrentHashMap<String, ImmutablePoint>(points);
        unmodifiableMap = Collections.unmodifiableMap(locations);
    }

    public Map<String, ImmutablePoint> getLocations() {
        return unmodifiableMap;
    }

    public ImmutablePoint getLocation(String id) {
        return locations.get(id);
    }

    /**
     *  Если GPS потом решит изменить location у какого-то авто,
     *  то в мэп это авто получит новые координаты, и, значит,
     *  клиент когда будет получать это значение из мапы -- также увидит LIVE данные.
     * @param id
     * @param x
     * @param y
     */
    public void setLocation(String id, int x, int y) {
        if (locations.replace(id, new ImmutablePoint(x, y)) == null)
            throw new IllegalArgumentException("invalid vehicle name: " + id);
    }

    // Alternate version of getLocations (Listing 4.8)
    public Map<String, ImmutablePoint> getLocationsAsStaticCopy() {
        return Collections.unmodifiableMap(
                new HashMap<String, ImmutablePoint>(locations));
        // втавит в новую коллекцию копию объектов из locations
        // т.е. будет копия, которая более независима от исходных данных из locations
        // и если будет изменено в исходных locations -- то в новой копии ничего не изменится.
    }
}

