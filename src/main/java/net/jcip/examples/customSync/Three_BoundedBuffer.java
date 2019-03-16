package net.jcip.examples.customSync;

import net.jcip.annotations.*;

/**
 * Bounded buffer using condition queues
 */
@ThreadSafe
public class Three_BoundedBuffer<V> extends BaseBoundedBuffer<V> {
    // CONDITION PREDICATE: not-full (!isFull())
    // CONDITION PREDICATE: not-empty (!isEmpty())
    public Three_BoundedBuffer() {
        this(100);
    }

    public Three_BoundedBuffer(int size) {
        super(size);
    }

    /*
    Короче, основной смысл такой:
    1) condition queue=wait set=очередь-список потоков, хранящийся у какого-то объекта
    Чтобы класть и дёргать потоки из этого списка, надо иметь лок у этого объекта. (ЛОК_1)

    2) condition predicate = какое-то логическое выражение, которое должно выполняться.
    имплементацией его(выр-я) будут переменные(поля) объекта, которые хранят его состояние.
    Поэтому чтобы проверять выполнение condition predicate -- надо иметь лок у этого объекта. (ЛОК_2)

    3) чтобы механизм с condition queues и condition predicate работал нужно !!одновременно!!
     смочь выполнить первые 2 условия (см.выше). А это возможно !!!только тогда!!! когда
      ЛОК_1 = ЛОК_2, а это значит, что объектом лока должен быть 1 и тот же объект.
     */

    // BLOCKS-UNTIL: not-full
    // т.е. если "not-full" -- то можно работать
    // или если FULL -- то блокируется
    public synchronized void put(V v) throws InterruptedException {
        while (isFull()) {
            wait(); // тут заснул - отпустил лок
            // wait() reacquires the lock before returning.
            // тут проснулся - снова взял лок
        }
        doPut(v);
        notifyAll();
    }

    // BLOCKS-UNTIL: not-empty
    // т.е. если "not-empty" -- то можно работать
    // или если EMPTY -- то блокируется
    public synchronized V take() throws InterruptedException {
        // tests the condition predicate (that the buffer is nonempty).
        while (isEmpty()) {
            this.wait(); // тут заснул - отпустил лок
            // wait() reacquires the lock before returning.
            // тут проснулся - снова взял лок
        }
        // If the buffer is indeed nonempty, it removes the first element,
        // which it can do because it still holds the lock guarding the buffer state.
        V v = doTake();
        this.notifyAll();
        return v;
    }

    // BLOCKS-UNTIL: not-full
    // Alternate form of put() using conditional notification
    public synchronized void alternatePut(V v) throws InterruptedException {
        while (isFull()) {
            wait();
        }
        boolean wasEmpty = isEmpty();
        doPut(v);
        if (wasEmpty) {
            notifyAll();
        }
    }
}
