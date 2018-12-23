package net.jcip.examples.threadPools.my2;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 */
public class My2ScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    private volatile My2ThreadFactory factory;

    public My2ScheduledThreadPoolExecutor(int corePoolSize, My2ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
        this.factory = threadFactory;
    }

    public int getCreated() {
        return factory.getCreated();
    }

    public int getAlive() {
        return factory.getAlive();
    }

    public int getUncaught() {
        return factory.getUncaught();
    }

    @Override
    public String toString() {
        return "Executor{" +
                "Created=" + getCreated() +
                ", Alive=" + getAlive() +
                ", Uncaught=" + getUncaught() +
                '}';
    }
}
