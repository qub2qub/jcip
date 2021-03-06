package net.jcip.examples.memoization;

import net.jcip.examples.LaunderThrowable;

import java.util.*;
import java.util.concurrent.*;

/**
 * Memoizing wrapper using FutureTask
 */
public class Memoizer3 <A, V> implements Computable<A, V> {
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer3(Computable<A, V> c) {
        this.c = c;
    }

    public V compute(final A arg) throws InterruptedException {
        Future<V> f = cache.get(arg);
        if (f == null) {
//            Callable<V> eval = new Callable<V>() {
//                public V call() throws InterruptedException {
//                    return c.compute(arg);
//                }
//            };
            FutureTask<V> ft = new FutureTask<>(() -> c.compute(arg)); // eval
            f = ft;
            cache.put(arg, ft);
            // второй также положил в мап, и затёр прежнюю фьючу
            // но каждый будет жать пока закончаися его собственные вычисления.
            ft.run(); // call to c.compute() happens here
        }
        try {
            return f.get();
        } catch (ExecutionException e) {
            throw LaunderThrowable.launderThrowable(e.getCause());
        }
    }
}
