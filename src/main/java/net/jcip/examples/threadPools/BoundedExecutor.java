package net.jcip.examples.threadPools;

import java.util.concurrent.*;

import net.jcip.annotations.*;

/**
 * Using a Semaphore to throttle task submission
 */
@ThreadSafe
public class BoundedExecutor {
    private final Executor executor;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor executor, int bound) {
        this.executor = executor;
        //set the bound on the semaphore to be equal to the pool size plus the number of queued tasks you want to allow
        this.semaphore = new Semaphore(bound);
    }

    public void submitTask(final Runnable command) throws InterruptedException {
        semaphore.acquire();
        try {
            executor.execute(() -> {
                try {
                    command.run();
                } finally {
                    semaphore.release();
                }
            });
        } catch (RejectedExecutionException e) {
            semaphore.release();
        }
    }

    private void callersRunPolicy() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 2, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(10));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
