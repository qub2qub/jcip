package net.jcip.examples.cancellation.timedRun;

import net.jcip.examples.LaunderThrowable;

class RethrowableTask implements Runnable {
    private final Runnable originalTask;
    RethrowableTask(Runnable originalTask) {
        this.originalTask = originalTask;
    }
    // Throwable is shared between the two threads, and so is declared volatile
    // to safely publish it from the task thread to the timedRun thread.
    private volatile Throwable errorThrownFromOriginalTask;

    public void run() {
        try {
            System.out.println("thread timedRun -- 1 -- start originalTask");
            originalTask.run();
            System.out.println("thread timedRun -- 2 -- finish originalTask");
        } catch (Throwable t) {
            System.out.println("thread timedRun -- 3 -- catch exc in RethrowableTask");
            this.errorThrownFromOriginalTask = t;
        }
    }

    void rethrow() {
        if (errorThrownFromOriginalTask != null) {
            System.out.println("thread timedRun -- 4 -- rethrow:" + errorThrownFromOriginalTask);
            throw LaunderThrowable.launderThrowable(errorThrownFromOriginalTask);
        }
    }
}
