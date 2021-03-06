package net.jcip.examples.cancellation.nonStandardCancel;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Encapsulating nonstandard cancellation in a Thread by overriding interrupt
 */
public class ReaderThread_NonstandardCancellation extends Thread {
    private static final int BUFFER_SIZE = 512;
    private final Socket socket;
    private final InputStream in;

    public ReaderThread_NonstandardCancellation(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
    }

    /*
     To facilitate terminating a user connection or shutting down the server,
     ReaderThread overrides interrupt to both:
     1) deliver a standard interrupt and 2) close the underlying socket;
     thus interrupting a ReaderThread makes it stop what it is doing
     whether it is blocked in read or in an interruptible blocking method.
     */
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException ignored) {
            // DO NOTHING
        } finally {
            super.interrupt();
        }
    }

    public void run() {
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            while (true) {
                int count = in.read(buf);
                if (count < 0) {
                    break;
                } else if (count > 0) {
                    processBuffer(buf, count);
                }
            }
        } catch (IOException e) {
            // Allow thread to exit
        }
    }

    public void processBuffer(byte[] buf, int count) {
        // DO NOTHING
    }
}
