package net.jcip.examples.shutdown;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UncaughtExceptionHandler that logs the exception.
 *
 * -- короче, исключение будет обработано в UncaughtExceptionHandler
 * только для тех задач, которые были запущены через execute().
 *
 * -- для тасков запущенных через submit() - ВСЕ (любые виды) исключений
 * будут обёрнуты в ExecutionException, которое придёт из Future.get().
 */
public class UEHLogger implements Thread.UncaughtExceptionHandler {

    public void uncaughtException(Thread t, Throwable e) {
        Logger logger = Logger.getAnonymousLogger();
        logger.log(Level.SEVERE, "Thread terminated with exception: " + t.getName(), e);
    }

    // Typical thread-pool worker thread structure.
    /*public void run() {
        Throwable thrown = null;
        try {
            while (!isInterrupted())
                runTask(getTaskFromWorkQueue());
        } catch (Throwable e) {
            thrown = e;
        } finally {
            threadExited(this, thrown);
        }
    }*/

}
