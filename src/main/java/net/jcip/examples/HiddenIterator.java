package net.jcip.examples;

import java.util.*;

import net.jcip.annotations.*;

/**
 * Iteration hidden within string concatenation
 */
public class HiddenIterator {
    @GuardedBy("this") private final Set<Integer> set = new HashSet<Integer>();

    public synchronized void add(Integer i) {
        set.add(i);
    }

    public synchronized void remove(Integer i) {
        set.remove(i);
    }

    public void addTenThings() {
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            add(r.nextInt());
        }
        // always lock a collection when printing it to log.
        System.out.println("DEBUG: added ten elements to: " + set);
        // If HiddenIterator wrapped the HashSet with a synchronizedSet,
        // encapsulating the synchronization, this sort of error would not occur.
    }
}
