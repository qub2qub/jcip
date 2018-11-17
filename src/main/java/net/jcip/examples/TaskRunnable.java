package net.jcip.examples;

import java.util.concurrent.*;

/**
 * Restoring the interrupted status so as not to swallow the interrupt
 */
public class TaskRunnable implements Runnable {
    BlockingQueue<Task> blockingQueue;

    public void run() {
        try {
            processTask(blockingQueue.take());
        } catch (InterruptedException e) {
            // restore interrupted status
            Thread.currentThread().interrupt();
        }
    }

    void processTask(Task task) {
        // Handle the task
    }

    interface Task {
    }
}
