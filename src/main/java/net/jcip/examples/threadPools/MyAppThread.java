package net.jcip.examples.threadPools;

import java.util.concurrent.atomic.*;
import java.util.logging.*;

/**
 * The interesting customization takes place in MyAppThread, which lets you provide a thread name,
 * sets a custom UncaughtExceptionHandler that writes a message to a Logger,
 * maintains statistics on how many threads have been created and destroyed,
 * and optionally writes a debug message to the log when a thread is created or terminates.
 */
public class MyAppThread extends Thread {
    public static final String DEFAULT_NAME = "MyAppThread";
    private static volatile boolean debugLifecycle = false;
    private static final AtomicInteger created = new AtomicInteger();
    private static final AtomicInteger alive = new AtomicInteger();
    private static final Logger log = Logger.getAnonymousLogger();

    public MyAppThread(Runnable r) {
        this(r, DEFAULT_NAME);
    }

    public MyAppThread(Runnable runnable, String name) {
        super(runnable, name + "-" + created.incrementAndGet());

        //А для ScheduledThreadPoolExecutor -- Исключения в задаче попадут в ExecutionExceptions у соответствующей Future.
        //А сюда видимо придут только исключения, которые будут в самом потоке шкедьюлера,
        // а не в том runnable, в котором будет выполняться задача.
        setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                // Сработает только для .execute(..) у следующих executor-ов:
                // newFixedThreadPool, newSingleThreadExecutor, newCachedThreadPool
                log.log(Level.SEVERE, "UNCAUGHT in thread " + t.getName(), e);
            }
        });
    }

    public void run() {
        // Copy debug flag to ensure consistent value throughout.
        boolean debug = debugLifecycle;
        if (debug) log.log(Level.FINE, "Created " + getName());
        try {
            alive.incrementAndGet();
            super.run();
        } finally {
            alive.decrementAndGet();
            if (debug) log.log(Level.FINE, "Exiting " + getName());
        }
    }

    public static int getThreadsCreated() {
        return created.get();
    }

    public static int getThreadsAlive() {
        return alive.get();
    }

    public static boolean getDebug() {
        return debugLifecycle;
    }

    public static void setDebug(boolean b) {
        debugLifecycle = b;
    }
}
