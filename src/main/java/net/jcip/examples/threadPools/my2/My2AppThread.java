package net.jcip.examples.threadPools.my2;

/**
 * Custom thread class that saves statistics to its factory.
 */
public class My2AppThread extends Thread {
    private volatile My2ThreadFactory threadFactory;

    public My2AppThread(Runnable runnable, String name, My2ThreadFactory threadFactory) {
        super(runnable, name + "-" + threadFactory.incrementCreated());
        this.threadFactory = threadFactory;
        setUncaughtExceptionHandler((t, e) -> {
            System.out.println("Thread UncaughtExceptionHandler: " + e.getMessage());
            threadFactory.incrementUncaught();
        });
    }

    @Override
    public void run() {
        try {
            threadFactory.incrementAlive();
            super.run();
        } catch (Throwable t) {
            threadFactory.incrementUncaught();
            throw t;
        } finally {
            threadFactory.decrementAlive();
        }
    }

}
