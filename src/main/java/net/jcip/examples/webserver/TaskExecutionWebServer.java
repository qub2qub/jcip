package net.jcip.examples.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Web server using a thread pool
 */
public class TaskExecutionWebServer {
    private static final int NTHREADS = 100;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(NTHREADS);

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            EXECUTOR.execute(() -> handleRequest(connection));
            /*Runnable task = new Runnable() {
                public void run() {
                    handleRequest(connection);
                }
            };
            EXECUTOR.execute(task);*/
        }
    }

    private static void handleRequest(Socket connection) {
        // request-handling logic here
    }
}
