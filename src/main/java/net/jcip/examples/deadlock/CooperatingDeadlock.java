package net.jcip.examples.deadlock;

import java.util.*;

import net.jcip.annotations.*;
import net.jcip.examples.vehicleTracker.ImmutablePoint;

/**
 * Lock-ordering deadlock between cooperating objects
 */
public class CooperatingDeadlock {
    // Warning: deadlock-prone!
    class Taxi {
        @GuardedBy("this") private ImmutablePoint location, destination;
        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized ImmutablePoint getLocation() {
            return location;
        }

        public synchronized void setLocation(ImmutablePoint location) {
            this.location = location;
            if (location.equals(destination)) {
                // вызывает другой синхронизированный метод.
                dispatcher.notifyAvailable(this);
            }
        }

        public synchronized ImmutablePoint getDestination() {
            return destination;
        }

        public synchronized void setDestination(ImmutablePoint destination) {
            this.destination = destination;
        }
    }

    class Dispatcher {
        @GuardedBy("this") private final Set<Taxi> taxis;
        @GuardedBy("this") private final Set<Taxi> availableTaxis;

        public Dispatcher() {
            taxis = new HashSet<Taxi>();
            availableTaxis = new HashSet<Taxi>();
        }

        public synchronized void notifyAvailable(Taxi taxi) {
            availableTaxis.add(taxi);
        }

        /**
         * Сначала кто-то вызовет taxi -> setLocations -> dipatcher -> notifyAll
         * а другой вызовет сналала dispatcher -> getImage и потом taxi -> getLocation.
         * и это будет дэдлок, как с left right
         */
        public synchronized Image getImage() {
            Image image = new Image();
            for (Taxi t : taxis) {
                image.drawMarker(t.getLocation());
            }
            return image;
        }
    }

    class Image {
        public void drawMarker(ImmutablePoint p) {
        }
    }
}
