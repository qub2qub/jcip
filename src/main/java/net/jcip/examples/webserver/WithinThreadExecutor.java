package net.jcip.examples.webserver;

import java.util.concurrent.*;

/**
 * Executor that executes tasks synchronously in the calling thread
 */
public class WithinThreadExecutor implements Executor {
    public void execute(Runnable r) {
        r.run();
    };
}
