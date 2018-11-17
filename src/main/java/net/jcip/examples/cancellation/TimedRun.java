package net.jcip.examples.cancellation;

import java.util.concurrent.*;
import static net.jcip.examples.LaunderThrowable.launderThrowable;

/**
 * Cancelling a task using Future
 */
public class TimedRun {
    private static final ExecutorService taskExec = Executors.newCachedThreadPool();

    public static void timedRun(Runnable r, long timeout, TimeUnit unit) throws InterruptedException {
        Future<?> task = taskExec.submit(r);
        try {
            task.get(timeout, unit);
        } catch (TimeoutException e) {
            // task will be cancelled below
//            To simplify coding, this version calls Future.cancel() unconditionally in a finally block,
//            taking advantage of the fact that cancelling a completed task has no effect.
        } catch (ExecutionException e) {
            // exception thrown in task; rethrow
            throw launderThrowable(e.getCause());
        } finally {
            // Harmless if task already completed, потому что
            // This attempt will fail if the task has already completed,
            // has already been cancelled, or could not be cancelled for some other reason.
            task.cancel(true); // interrupt if running
        }
    }
}
