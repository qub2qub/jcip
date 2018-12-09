package net.jcip.examples.logging;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.*;

/**
 * Producer-consumer logging service with no shutdown support
 */
public class LogWriter {
    private final BlockingQueue<String> queue;
    private final LoggerThread logger;
    private static final int CAPACITY = 1000;

    public LogWriter(Writer writer) {
        this.queue = new LinkedBlockingQueue<>(CAPACITY);
        this.logger = new LoggerThread(writer);
    }

    public void start() {
        logger.start();
    }

    public void log(String msg) throws InterruptedException {

        queue.put(msg); // обычный флоу

        // 2й способ завершить, но тоже ненадёжный
        // зашли и сначала было shutdownRequested=false и логер как бы принял собщение,
        // но очередь была полная и мы заблокировались, и в это время стало shutdownRequested=true
        // и получится что мы потом таки положим в очередь, но уже после shutdown-а логера - что неверно.
//        if (!shutdownRequested) queue.put(msg);
//        else throw new IllegalStateException("logger is shut down");
        /*
        However, this approach has race conditions that make it unreliable:
        CHECK-THEN-ACT SEQUENCE: producers could observe that the service has not yet been shut down
        but still queue messages after the shutdown, again with the risk that
        the producer might get blocked in log and never become unblocked.
         */
    }

    private class LoggerThread extends Thread {
        private final PrintWriter writer; // PrintWriter is thread-safe

        public LoggerThread(Writer writer) {
            this.writer = new PrintWriter(writer, true); // autoflush
        }

        public void run() {
            try {
                while (true) {
                    writer.println(queue.take());
                }
            } catch (InterruptedException ignored) {
                // 1й способ завершить, но ненадёжный
                // можно было тут выйти, когда пришло бы InterruptedException из queue
                /*
                Such an abrupt shutdown discards log messages that might be waiting to be written to the log, but, MORE IMPORTANTLY,
                THREADS BLOCKED IN LOG BECAUSE THE QUEUE IS FULL WILL NEVER BECOME UNBLOCKED.
                CANCELLING A PRODUCER-CONSUMER activity requires CANCELLING BOTH the PRODUCERS AND THE CONSUMERS.
                Interrupting the logger thread deals with the consumer,
                but because the producers in this case are not dedicated threads, cancelling them is harder.
                 */
            } finally {
                writer.close();
            }
        }
    }
}
