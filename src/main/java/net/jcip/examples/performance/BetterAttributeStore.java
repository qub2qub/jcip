package net.jcip.examples.performance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.*;

import net.jcip.annotations.*;

/**
 * Reducing lock duration
 */
@ThreadSafe
public class BetterAttributeStore {
    @GuardedBy("this")
    private final Map<String, String> attributes = new HashMap<>();
    // лучше конечно просто заменить многопоточной коллекцией и убрать synchronized
//    private final Map<String, String> attributes = new ConcurrentHashMap<>();

    public boolean userLocationMatches(String name, String regexp) {
        String key = "users." + name + ".location";
        String location;
        synchronized (this) {
            location = attributes.get(key);
        }
        return location != null && Pattern.matches(regexp, location);
    }
}
