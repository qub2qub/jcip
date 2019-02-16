package net.jcip.examples.performance;

import java.util.*;
import java.util.regex.*;

import net.jcip.annotations.*;

/**
 * Holding a lock longer than necessary
 */
@ThreadSafe
public class AttributeStore {
    @GuardedBy("this")
    private final Map<String, String> attributes = new HashMap<>();

    public synchronized boolean userLocationMatches(String name, String regexp) {
        String key = "users." + name + ".location";
        String location = attributes.get(key);
        return location != null && Pattern.matches(regexp, location);
    }
}
