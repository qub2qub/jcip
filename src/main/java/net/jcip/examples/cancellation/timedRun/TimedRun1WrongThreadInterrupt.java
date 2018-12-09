package net.jcip.examples.cancellation.timedRun;

import java.util.concurrent.*;

/**
 * InterruptBorrowedThread
 * Scheduling an interrupt on a borrowed thread
 */
public class TimedRun1WrongThreadInterrupt {
    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool(1);

    public static void timedRun(Runnable r, long timeout, TimeUnit unit) {
        // 1) запланировать прерывание потока (НЕ НАДО ТАК ДЕЛАТЬ!)
        cancelExec.schedule(Thread.currentThread()::interrupt, timeout, unit);
        // 2) Выполнить свою задачу из Runnable: смотри "r.run();" ниже
        r.run();
        // через какое-то время прервёт поток, тем самым если будут unchecked exceptions --
        // то в вызвавшем метод timedRun() потоке появятся unchecked exc.
        // Это вроде как норм. Но тут проблема в том,
        // что taskThread.interrupt() прервёт выполняющий поток. (так делеать НЕ надо!)
        // it violates the rules: you should know a thread's interruption policy before interrupting it
    }
}
