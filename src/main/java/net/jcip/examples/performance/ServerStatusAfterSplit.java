package net.jcip.examples.performance;

import java.util.*;

import net.jcip.annotations.*;

/**
 * ServerStatus refactored to use split locks
 */
@ThreadSafe
public class ServerStatusAfterSplit {
    // или можно заменить на thread-safe коллекции, тогда вообще не надо доп. синхронизация
    @GuardedBy("users") public final Set<String> users;
    @GuardedBy("queries") public final Set<String> queries;

    public ServerStatusAfterSplit() {
        users = new HashSet<String>();
        queries = new HashSet<String>();
    }

    public void addUser(String u) {
        synchronized (users) {
            users.add(u);
        }
    }

    public void addQuery(String q) {
        synchronized (queries) {
            queries.add(q);
        }
    }

    public void removeUser(String u) {
        synchronized (users) {
            users.remove(u);
        }
    }

    public void removeQuery(String q) {
        synchronized (users) {
            queries.remove(q);
        }
    }
}
