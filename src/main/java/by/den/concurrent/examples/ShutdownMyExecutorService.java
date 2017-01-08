package by.den.concurrent.examples;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ShutdownMyExecutorService {
/**
     * Waits 6 seconds for search tasks to be completed
     * and then shutdowns the ExecutorService
     *
     * @param pool the instance of ExecutorService
     */
    private void shutdownAndAwaitTermination(ExecutorService pool) {
        if (pool != null) {
            pool.shutdown(); // Disable new tasks from being submitted
            try {
                // Wait a while for existing tasks to terminate
                if (!pool.awaitTermination(6, TimeUnit.SECONDS)) {
                    // if timeout elapsed, and tasks did not finish
                    // если время ожидания истекло, а задачи ещё не закончились
                    pool.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!pool.awaitTermination(2, TimeUnit.SECONDS))
                        System.out.println("Search tasks did not terminate.");
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                pool.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            } finally {
                if (!pool.isTerminated()) {
                    System.out.println("Search pool did not terminate.");
                }
                pool.shutdownNow();
            }
        }
    }
}