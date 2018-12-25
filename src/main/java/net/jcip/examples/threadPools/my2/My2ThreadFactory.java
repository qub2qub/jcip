package net.jcip.examples.threadPools.my2;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom factory with statistics about its threads.
 */
public class My2ThreadFactory implements ThreadFactory {
    private final String poolName;
    private final AtomicInteger created = new AtomicInteger();
    private final AtomicInteger alive = new AtomicInteger();
    private final AtomicInteger uncaught = new AtomicInteger();

    public My2ThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        My2AppThread my2AppThread = new My2AppThread(runnable, poolName, this);
        System.out.println("my2AppThread = " + my2AppThread);
        return my2AppThread;
    }

    public int incrementCreated() {
        return created.incrementAndGet();
    }

    public int incrementUncaught() {
        return uncaught.incrementAndGet();
    }

    public int incrementAlive() {
        return alive.incrementAndGet();
    }

    public int decrementAlive() {
        return alive.decrementAndGet();
    }

    public int getCreated() {
        return created.get();
    }

    public int getAlive() {
        return alive.get();
    }

    public int getUncaught() {
        return uncaught.get();
    }
}
