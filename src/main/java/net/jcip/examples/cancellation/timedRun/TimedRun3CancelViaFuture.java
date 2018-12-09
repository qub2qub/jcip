package net.jcip.examples.cancellation.timedRun;

import java.util.concurrent.*;
import static net.jcip.examples.LaunderThrowable.launderThrowable;

/**
 * Cancelling a task using Future
 */
public class TimedRun3CancelViaFuture {
    private static final ExecutorService taskExec = Executors.newCachedThreadPool();

    public static void timedRun(Runnable r, long timeout, TimeUnit unit) throws InterruptedException {
        Future<?> task = taskExec.submit(r);
        try {
            // good practice: cancelling tasks whose result is no longer needed (after timeout).
            task.get(timeout, unit);
        } catch (TimeoutException e) {
            // If .get() terminates with a TimeoutException, the task is cancelled via its Future.
            // To simplify coding, this version calls Future.cancel() unconditionally in a finally block,
            // taking advantage of the fact that cancelling a completed task has no effect.
        } catch (ExecutionException e) {
            // If the UNDERLYING COMPUTATION throws an exception PRIOR to cancellation, it is rethrown
            throw launderThrowable(e.getCause());
        } finally {
            // Harmless if task already completed, потому что
            // This attempt will fail if the task has already completed,
            // has already been cancelled, or could not be cancelled for some other reason.
            task.cancel(true); // interrupt if running
        }
    }
}
