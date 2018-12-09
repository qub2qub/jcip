package net.jcip.examples.cancellation;

import static java.util.concurrent.TimeUnit.SECONDS;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;

import net.jcip.annotations.*;

/**
 * Using a volatile field to hold cancellation state
 */
@ThreadSafe
public class PrimeGenerator implements Runnable {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    @GuardedBy("this")
    private final List<BigInteger> primes = new ArrayList<>();

    private volatile boolean cancelled;

    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled) {
            p = p.nextProbablePrime();
            synchronized (this) {
                primes.add(p);
            }
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public synchronized List<BigInteger> get() {
        return new ArrayList<>(primes);
    }

    static List<BigInteger> aSecondOfPrimes() throws InterruptedException {
        PrimeGenerator generator = new PrimeGenerator();
        exec.execute(generator);
        try {
            SECONDS.sleep(1);
        } finally {
            generator.cancel();
        }
        return generator.get();
    }

    public static void main(String[] args) {
        try {
            List<BigInteger> primes = aSecondOfPrimes();
            System.out.printf("primes = %s\n", primes);
            exec.shutdown(); // мой способ остановить потоки из ExecutorService
            //shutdownNow() пробует остановить и запущенные задачи, если они есть.
            // поэтому чтобы дождаться выполнения запущенных и уже запланированных задач --
            // надо вызывать метод shutdown()
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
