package net.jcip.examples.memoization;

import net.jcip.examples.LaunderThrowable;

import java.util.concurrent.*;

/**
 * Memoizer
 * <p/>
 * Final implementation of Memoizer
 *
 * @author Brian Goetz and Tim Peierls
 */
public class Memoizer <A, V> implements Computable<A, V> {
    private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();
    private final Computable<A, V> c;

    public Memoizer(Computable<A, V> c) {
        this.c = c;
    }

    public V compute(final A arg) throws InterruptedException {
        while (true) {
            Future<V> f = cache.get(arg);
            if (f == null) {
                Callable<V> eval = new Callable<V>() {
                    public V call() throws InterruptedException {
                        return c.compute(arg);
                    }
                };
                FutureTask<V> ft = new FutureTask<V>(eval);
                // т.е. если не вернулся нулл = то новое значение мы не засетали.
                // а если вернулся нулл = то новое значение мы засетали.
                f = cache.putIfAbsent(arg, ft); // пробуем добавить
                // вопрос: а когда мы стартуем фьючу в первый раз?
                // ответ: когда вернулся нулл, т.е. раньше в мапе такой задачи не было.
                // а если вернулось какое-то значение -- то в этот if не заходим, а берём только f.get()
                if (f == null) { // если такая ФьючеТакс уже лежит в мапе -- то ничего НЕ старуем
                    f = ft;
                    ft.run();
                }
            }
            try {
                return f.get();
            } catch (CancellationException e) {
                cache.remove(arg, f); // удаляем из кэша старую фьючу, т.к. он закэнселилась.
            } catch (ExecutionException e) {
                throw LaunderThrowable.launderThrowable(e.getCause());
            }
        }
    }
}