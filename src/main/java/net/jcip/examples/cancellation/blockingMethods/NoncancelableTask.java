package net.jcip.examples.cancellation.blockingMethods;

import java.util.concurrent.*;

/**
 * Noncancelable task that restores interruption before exit
 */
public class NoncancelableTask {
    public Task getNextTask(BlockingQueue<Task> queue) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return queue.take();
                } catch (InterruptedException e) {
                    interrupted = true; // save the interruption status locally
                    // fall through and retry = провал (потерьпеть неудачу) и повторить попытку
                }
            }
        } finally {
            if (interrupted) { // restore the interruption status just before returning
                Thread.currentThread().interrupt();
            }
        }
    }

    interface Task {
    }
}
