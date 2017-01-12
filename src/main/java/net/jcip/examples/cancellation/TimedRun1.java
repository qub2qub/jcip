package net.jcip.examples.cancellation;

import java.util.concurrent.*;

/**
 * InterruptBorrowedThread
 * <p/>
 * Scheduling an interrupt on a borrowed thread
 *
 * @author Brian Goetz and Tim Peierls
 */
public class TimedRun1 {
    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool(1);

    public static void timedRun(Runnable r, long timeout, TimeUnit unit) {

        final Thread taskThread = Thread.currentThread();
        // через какое-то время прервёт потом, тем самым
        cancelExec.schedule(new Runnable() {
            public void run() {
                taskThread.interrupt();
            }
        }, timeout, unit);
        // если будут unchecked exc -- то в вызвавшем метод timedRun() потоке
        // появятся unchecked exc. Это вроде как норм.
        // Но тут проблема в том, что taskThread.interrupt() прервёт выполняющий поток.
        // так делеать НЕ надо!
        // it violates the rules: you should know a thread's interruption policy before interrupting it
        r.run();
    }
}
