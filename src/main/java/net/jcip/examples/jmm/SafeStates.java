package net.jcip.examples.jmm;

import java.util.*;

import net.jcip.annotations.*;

/**
 * Initialization safety for immutable objects:
 * SafeStates could be safely published even through unsafe lazy initialization or stashing a reference
 * to a SafeStates in a public static field with no synchronization,
 * even though it uses no synchronization and relies on the non-thread-safe HashSet.
 */
@ThreadSafe
public class SafeStates {
    /**
     * However, a number of small changes to SafeStates would take away its thread safety.
     * If states were not final, or if any method other than the constructor modified its contents
     */
    private final Map<String, String> states;

    public SafeStates() {
        states = new HashMap<>();
        states.put("alaska", "AK");
        states.put("alabama", "AL");
        /*...*/
        states.put("wyoming", "WY");
    }

    public String getAbbreviation(String s) {
        return states.get(s);
    }
}
