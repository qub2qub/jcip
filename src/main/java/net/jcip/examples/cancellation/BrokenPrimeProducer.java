package net.jcip.examples.cancellation;

import java.math.BigInteger;
import java.util.concurrent.*;

/**
 * Unreliable cancellation that can leave producers stuck in a blocking operation
 */
class BrokenPrimeProducer extends Thread {
    private final BlockingQueue<BigInteger> queue;
    private volatile boolean cancelled = false;

    BrokenPrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (!cancelled) // не сработает, т.к. будет заблокрован в queue
                queue.put(p = p.nextProbablePrime());
        } catch (InterruptedException consumed) {
            // но при exc код перейдёт сюда и прога закончится.
            // но, например, когда очередь будет забита -- екс не будет, и сюда не зайдет. очередь просто заблокируется.
        }
    }

    public void cancel() {
        cancelled = true;
    }
}

