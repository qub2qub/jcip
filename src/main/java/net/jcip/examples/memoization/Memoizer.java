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
    private final Computable<A, V> computable;

    public Memoizer(Computable<A, V> computable) {
        this.computable = computable;
    }

    public V compute(final A arg) throws InterruptedException {
        while (true) {
            Future<V> f = cache.get(arg);
            if (f == null) {
                Callable<V> eval = () -> computable.compute(arg);
                FutureTask<V> futureTask = new FutureTask<>(eval);
                // т.е. если не вернулся нулл = то новое значение мы не засетали.
                // т.к. там уже было какое-то значение.
                // а если вернулся нулл = то новое значение мы засетали, т.к. там ничего не лежало
                f = cache.putIfAbsent(arg, futureTask); // пробуем добавить
                // вопрос: а когда мы стартуем фьючу в первый раз?
                // ответ: когда вернулся нулл, т.е. раньше в мапе такой задачи не было.
                // а если вернулось какое-то значение -- то в этот if не заходим, а берём только f.get()
                if (f == null) { // будет true если в мапе ничего не лежало для этого ключа
                    // а если такая ФьючеТакс уже лежит в мапе -- то ничего НЕ старуем
                    f = futureTask;
                    futureTask.run();
                }
            }
            try {
                return f.get();
            } catch (CancellationException e) {
                cache.remove(arg, f); // удаляем из кэша старую фьючу, т.к. она закэнселилась.
            } catch (ExecutionException e) {
                throw LaunderThrowable.launderThrowable(e.getCause());
            }
        }
    }
}
