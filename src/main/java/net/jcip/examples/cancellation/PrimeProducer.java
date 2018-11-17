package net.jcip.examples.cancellation;

import java.math.BigInteger;
import java.util.concurrent.*;

/**
 * Using interruption for cancellation
 */
public class PrimeProducer extends Thread {
//public class PrimeProducer implements Runnable {
    private final BlockingQueue<BigInteger> queue;

    PrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (!Thread.currentThread().isInterrupted())
                queue.put(p = p.nextProbablePrime());
            System.out.print(p+", ");
        } catch (InterruptedException consumed) {
            /* Allow thread to exit */
        }
    }

    public void cancel() {
        interrupt();
    }
}
