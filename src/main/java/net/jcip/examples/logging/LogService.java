package net.jcip.examples.logging;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.*;

import net.jcip.annotations.*;

/**
 * Adding reliable cancellation to LogWriter
 */
public class LogService {
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;
    @GuardedBy("this") private boolean isShutdown;
    @GuardedBy("this") private int reservations;

    public LogService(Writer writer) {
        this.queue = new LinkedBlockingQueue<String>();
        this.loggerThread = new LoggerThread();
        this.writer = new PrintWriter(writer);
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
        }
        loggerThread.interrupt();
    }

    public void log(String msg) throws InterruptedException {
        synchronized (this) {
            if (isShutdown) {
                // и больше нельзя будет добавить в очередь на вывод в лог
                throw new IllegalStateException(/*...*/);
            }
            // если не завершается -- то можно добавлять в лог.
            ++reservations;
        }
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        public void run() {
            try {
                while (true) {
                    try {
                        synchronized (LogService.this) {
                            if (isShutdown && reservations == 0)
                                // когда очистим все reservations -- можно будет закрыться/остановиться
                                break;
                        }
                        // ожидаем если есть блок
                        String msg = queue.take();
                        synchronized (LogService.this) {
                            // взяли строку в лог, уменьшаем каунтер
                            --reservations;
                        }
                        // пишем сообщение в лог
                        writer.println(msg);
                        // заново проходим весь цикл while
                    } catch (InterruptedException e) { /* retry */
                    }
                }
            } finally {
                writer.close();
            }
        }
    }
}

