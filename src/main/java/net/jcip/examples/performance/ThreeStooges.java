package net.jcip.examples.performance;

import java.util.*;

import net.jcip.annotations.*;

/**
 * Immutable class built out of mutable underlying objects,
 * demonstration of candidate for lock elision
 */
@Immutable
 public final class ThreeStooges {
    private final Set<String> stooges = new HashSet<String>();

    public ThreeStooges() {
        stooges.add("Moe");
        stooges.add("Larry");
        stooges.add("Curly");
    }

    public boolean isStooge(String name) {
        return stooges.contains(name);
    }

    public String getStoogeNames() {
        List<String> stooges = new Vector<String>();
        stooges.add("Moe");
        stooges.add("Larry");
        stooges.add("Curly");
        return stooges.toString();
    }
}
