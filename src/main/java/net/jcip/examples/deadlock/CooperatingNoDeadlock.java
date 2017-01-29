package net.jcip.examples.deadlock;

import java.util.*;

import net.jcip.annotations.*;
import net.jcip.examples.vehicleTracker.ImmutablePoint;

/**
 * CooperatingNoDeadlock
 * <p/>
 * Using open calls to avoiding deadlock between cooperating objects
 *
 * @author Brian Goetz and Tim Peierls
 */
class CooperatingNoDeadlock {
    @ThreadSafe
    class Taxi {
        @GuardedBy("this") private ImmutablePoint location, destination;
        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized ImmutablePoint getLocation() {
            return location;
        }

        public void setLocation(ImmutablePoint location) {
            boolean reachedDestination;
            synchronized (this) {
                this.location = location;
                reachedDestination = location.equals(destination);
            }
            if (reachedDestination) {
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

    @ThreadSafe
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

        public Image getImage() {
            Set<Taxi> copy;
            // чтобы избежать concurrentModifExc самого сета.
            synchronized (this) {
                copy = new HashSet<Taxi>(taxis);
            }
            Image image = new Image();
            // сначала сделали копию всех такси, чтобы получить срез
            // чтобы не надо было вызывать getLocation из синхрониз.блока
            // но они же сделали только копию ссылки на сет,
            // поля в каждом такси также могут буть изменены из других потоков.
            for (Taxi t : copy) {
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