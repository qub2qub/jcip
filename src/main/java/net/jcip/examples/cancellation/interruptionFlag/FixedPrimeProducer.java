package net.jcip.examples.cancellation.interruptionFlag;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * Using interruption for cancellation.
 */
public class FixedPrimeProducer {
    private final BlockingQueue<BigInteger> queue;

    FixedPrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            // makes it more responsive to interruption because it checks for interruption before starting the lengthy task
            // When calls to interruptible blocking methods are not frequent enough
            // to deliver the desired responsiveness, explicitly testing the interrupted status can help.
            while (!Thread.currentThread().isInterrupted()) {
                queue.put(p = p.nextProbablePrime());
            }
        } catch (InterruptedException consumed) {
            // Allow thread to exit
        }
    }

    public void cancel() {
        Thread.currentThread().interrupt();
    }
}
