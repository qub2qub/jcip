package net.jcip.examples.logging;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Logging service that uses an ExecutorService to proper shutdown.
 * More sophisticated programs are likely to encapsulate an ExecutorService
 * behind a higher-level service that provides its own lifecycle methods.
 * Encapsulating an ExecutorService extends the ownership chain from application to service to thread
 * by adding another link; each member of the chain manages the lifecycle of the services or threads it owns.
 */
public class LogService2ExecutorService {
    private static final long TIMEOUT = 10;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private final ExecutorService executor = newSingleThreadExecutor();
    private final PrintWriter writer;

    public LogService2ExecutorService(Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    public void start() {}
    public void stop() throws InterruptedException {
        try {
            executor.shutdown();
            executor.awaitTermination(TIMEOUT, UNIT);
        } finally {
            writer.close();
        }
    }

    public void log(String msg) {
        try {
            executor.execute(new WriteTask(msg));
        } catch (RejectedExecutionException ignored) {
            // когда уже вызвали стоп -- то при добавлении будет RejectedExecutionException
            // игнорируем и не пишем в лог.
        }
    }

    private class WriteTask implements Runnable {
        private final String msg;

        public WriteTask(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            writer.append(msg);
        }
    }
}
