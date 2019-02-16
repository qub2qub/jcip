package net.jcip.examples.performance;

import java.util.*;

import net.jcip.annotations.*;

/**
 * Candidate for lock splitting
 */
@ThreadSafe
public class ServerStatusBeforeSplit {
    @GuardedBy("this")
    private final Set<String> users;
    @GuardedBy("this")
    private final Set<String> queries;

    public ServerStatusBeforeSplit() {
        users = new HashSet<>();
        queries = new HashSet<>();
    }

    public synchronized void addUser(String u) {
        users.add(u);
    }

    public synchronized void addQuery(String q) {
        queries.add(q);
    }

    public synchronized void removeUser(String u) {
        users.remove(u);
    }

    public synchronized void removeQuery(String q) {
        queries.remove(q);
    }
}
