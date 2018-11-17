package net.jcip.examples.webserver;

import java.util.concurrent.*;

/**
 * Executor that starts a new thread for each task
 */
public class ThreadPerTaskExecutor implements Executor {
    public void execute(Runnable r) {
        new Thread(r).start();
    };
}
