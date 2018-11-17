package net.jcip.examples.cancellation;

import java.math.BigInteger;
import java.util.concurrent.*;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static net.jcip.examples.LaunderThrowable.launderThrowable;

/**
 * Interrupting a task in a dedicated thread
 */
public class TimedRun2 {
    private static final ScheduledExecutorService cancelExec = newScheduledThreadPool(1);

    public static void timedRun(final Runnable r, long timeout, TimeUnit unit) throws InterruptedException {

        class RethrowableTask implements Runnable {
            // Throwable is shared between the two threads, and so is declared volatile
            // to safely publish it from the task thread to the timedRun thread.
            private volatile Throwable t;
            public void run() {
                try {
                    System.out.println("thread timedRun -- 1 -- start");
                    r.run();
                    System.out.println("thread timedRun -- 2 -- finish");
                } catch (Throwable t) {
                    System.out.println("thread timedRun -- 3 -- catch exc");
                    this.t = t;
                }
            }
            void rethrow() {
                if (t != null) {
                    System.out.println("thread timedRun -- 4 -- rethrow:"+t);
                    throw launderThrowable(t);
                }
            }
        }

        RethrowableTask task = new RethrowableTask();
        final Thread taskThread = new Thread(task);
        taskThread.start();
        cancelExec.schedule(new Runnable() {
            public void run() {
                System.out.println("\n\n >>>>>> спец.поток для отсчёта таймаута: " +
                        "thread="+Thread.currentThread()+
                        "\nInterrupt after timeout <<<<<<<\n");
                taskThread.interrupt();
            }
        }, timeout, unit);

        System.out.println("cur thread="+Thread.currentThread());
        System.out.println("Joined for ("+unit.toSeconds(timeout)+")sec taskThread="+taskThread);
        // что здесь далают?
        // ждут N времени, пока закончится поток taskThread
        // но тут непонятно:  we don't know if control was returned
        // because the thread exited normally or because the join timed out.
        taskThread.join(unit.toMillis(timeout));
        System.out.println("перед rethrow из task in cur thread="+Thread.currentThread());
        // без паузы из-за join() -- нечего было бы rethrow-ать
        task.rethrow();
        // shutdown executor, т.к. его поток всё ещё висит после окончания проги.
        cancelExec.shutdown();
    }

    public static void main(String[] args) {

        class MainRun implements Runnable {
            @Override
            public void run() {
                System.out.println("__MainRun__ start");
                try {
                    Thread.sleep(1000);
                    System.out.println("__MainRun__ middle1 before exc, thread active count="+Thread.activeCount());
                    // закоментируй чтобы дождаться таймаута из timedRun()
//                    if(true) throw new RuntimeException(" unexpected unchecked exc !!");
                    Thread.sleep(1000);
                    System.out.println("__MainRun__ middle2 after exc");
                    Thread.sleep(1000);
                    System.out.println("__MainRun__ middle3 middle fin");
                    BigInteger one = BigInteger.ONE;
                    // to get timeout -- use "while (true)" но при этом поток всё равно продолжит выполнение
//                    while (true) {
                    // а тут прервётся, т.к. отреагирует на taskThread.interrupt() из timedRun
                    while (!Thread.currentThread().isInterrupted()) {
                        System.out.print('.');
                        one = one.nextProbablePrime();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("__MainRun__ finally end");
                }
            }
        }
    	// 1) Main Thread - 1й исходный поток, создаёт новую задачу
        MainRun mainRun = new MainRun();
        try {
            //2) запуск во втором отоке
            // если поставить 2сек -- то прервём sleep в главной задаче MainRun
            // если 4сек -- то дожёмся тригера таймаута из timedRun
            timedRun(mainRun, 4, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // 3) Main Thread handles InterruptedException
            System.out.println(" Main Thread handles InterruptedException ");
            e.printStackTrace();
        }
    }
}
