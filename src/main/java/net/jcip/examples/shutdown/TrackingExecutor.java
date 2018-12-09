package net.jcip.examples.shutdown;

import java.util.*;
import java.util.concurrent.*;

/**
 * ExecutorService that keeps track of cancelled tasks after shutdown
 */
public class TrackingExecutor extends AbstractExecutorService {

    private final ExecutorService executor;

    private final Set<Runnable> tasksCancelledAtShutdown = Collections.synchronizedSet(new HashSet<>());

    public TrackingExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void shutdown() {
        executor.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return executor.shutdownNow();
    }

    public boolean isShutdown() {
        return executor.isShutdown();
    }

    public boolean isTerminated() {
        return executor.isTerminated();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

    public List<Runnable> getCancelledTasks() {
        if (!executor.isTerminated()) {
            throw new IllegalStateException(/*...*/);
        }
        return new ArrayList<>(tasksCancelledAtShutdown);
    }

    public void execute(final Runnable runnable) {
        executor.execute(() -> {
            try {
                runnable.run();
            } finally {
                // In order for this technique to work, the tasks must preserve the thread's interrupted status
                // when they return, which well behaved tasks will do anyway.
                if (isShutdown() && Thread.currentThread().isInterrupted())
                    tasksCancelledAtShutdown.add(runnable);
            }
        });
        /* executor.execute(new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } finally {
                    if (isShutdown() && Thread.currentThread().isInterrupted())
                        tasksCancelledAtShutdown.add(runnable);
                }
            }
        }); */
    }
}
