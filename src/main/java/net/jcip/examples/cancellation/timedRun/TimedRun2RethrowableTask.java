package net.jcip.examples.cancellation.timedRun;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * Interrupting a task in a dedicated thread
 */
public class TimedRun2RethrowableTask {
    private static final ScheduledExecutorService cancelExec = newScheduledThreadPool(1);

    static void timedRun(final Runnable r, long timeout, TimeUnit unit) throws InterruptedException {

        RethrowableTask rethrowableTask = new RethrowableTask(r);
        final Thread taskThread = new Thread(rethrowableTask);
        taskThread.start();
        cancelExec.schedule(() -> {
            System.out.println(
                    "\n\n >>>>>> спец.поток для отсчёта таймаута: thread="
                    + Thread.currentThread() + "\nInterrupt after timeout <<<<<<<\n"
            );
            // запрограммировать прерывание конкрентного потока через N времени
            taskThread.interrupt();
        }, timeout, unit);

        System.out.println("cur thread=" + Thread.currentThread());
        System.out.println("Joined for (" + unit.toSeconds(timeout) + ")sec taskThread=" + taskThread);
        // что здесь далают?
        // ждут N времени, в течение которого taskThread может вычислять.
        // но тут непонятно:  we don't know if control was returned
        // because the thread exited normally or because the join timed out.
        // поэтому потом надо проверить состояние в RethrowableTask.errorThrownFromOriginalTask
        taskThread.join(unit.toMillis(timeout));
        System.out.println("\nперед rethrow из rethrowableTask in cur thread=" + Thread.currentThread());
        // без паузы из-за join() -- нечего было бы rethrow-ать
        // если есть какая-то errorThrownFromOriginalTask -- то выбросить её дальше.
        rethrowableTask.rethrow();
    }

    public static void main(String[] args) {
        // 1) Main Thread - 1й исходный поток, создаёт новую задачу
        MainRun mainTaskWithLimitedTime = new MainRun();
        try {
            //2) запускаем главную задачу во втором потоке, ограничиваем время на её выполнение.
            // если поставить 2сек -- то прервём sleep в главной задаче MainRun
            // если 3сек или 4 сек -- то дожёмся тригера таймаута из timedRun
            timedRun(mainTaskWithLimitedTime, 3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // 3) Main Thread handles InterruptedException
            System.out.println(" Main Thread handles InterruptedException ");
            e.printStackTrace();
        } finally {
            // shutdown executor, т.к. его поток всё ещё висит после окончания проги.
            cancelExec.shutdown();
        }
    }
}
