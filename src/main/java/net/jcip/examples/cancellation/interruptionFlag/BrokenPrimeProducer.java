package net.jcip.examples.cancellation.interruptionFlag;

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
            while (!cancelled) { // не сработает, т.к. будет заблокрован в queue
                // а блокирующий метод в queue отреагирует на Thread.currentThread().isInterrupted()
                // и выбросит InterruptedException, т.е. имеем 2 cancellation points
                queue.put(p = p.nextProbablePrime());
            }

        } catch (InterruptedException consumed) {
            // при InterruptedException код перейдёт сюда и прога закончится.
            // на всякий случай можно выставить в true, т.к. сюда мы попадём
            // из прерывания блокирующего метода.
            cancelled = true;
            // а когда очередь забита -- она заблокируется,
            // и мы не сможем прочитать cancelled флаг, но тут нам поможет
            // InterruptedException из блокирующего метода при установке Thread.currentThread().interrupt();
        }
    }

    public void cancel() {
        cancelled = true;
    }

    void consumePrimes() throws InterruptedException {
        BlockingQueue<BigInteger> primes = new LinkedBlockingQueue<>();
        BrokenPrimeProducer producer = new BrokenPrimeProducer(primes);
        producer.start();
        // The producer thread generates primes and places them on a blocking queue.
        // If the producer gets ahead of the consumer, the queue will fill up and `queue.put()` will block.
        try {
            while (needMorePrimes()) consume(primes.take());
        } finally {
            // т.е. вызовем .cancel() но producer может не остановиться, если очередь всё ещё полная
            // и он ждёт, т.е. заблокирован на queue.put() чтобы в неё положить результат,
            // и только поьлм producer сможет проверить флаг.
            producer.cancel();
        }
    }

    private void consume(BigInteger prime) {
        // do smt
    }

    private boolean needMorePrimes() {
        return false;
    }
}

