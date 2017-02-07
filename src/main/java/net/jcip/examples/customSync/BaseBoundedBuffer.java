package net.jcip.examples.customSync;

import net.jcip.annotations.*;

/**
 * BaseBoundedBuffer
 * <p/>
 * Base class for bounded buffer implementations
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public abstract class BaseBoundedBuffer <V> {
    @GuardedBy("this") private final V[] buf;
    @GuardedBy("this") private int tail;
    @GuardedBy("this") private int head;
    @GuardedBy("this") private int count;

    protected BaseBoundedBuffer(int capacity) {
        this.buf = (V[]) new Object[capacity];
    }

    protected synchronized final void doPut(V v) {
        buf[tail] = v;
        if (++tail == buf.length) {
            tail = 0;
        }
        ++count;
     /*
     т.е. номер ячейки всё время перемещается на следующий,
     с конца переходит на первый.
     т.о. данные записываются в каждую ячейку по порядку
     и достаюся также по порядку.
      Но где-то тут подвох...
     */
    }

    protected synchronized final V doTake() {
        V v = buf[head];
        buf[head] = null;
        if (++head == buf.length) {
            head = 0;
        }
        --count;
        return v;
    }

    public synchronized final boolean isFull() {
        return count == buf.length;
    }

    public synchronized final boolean isEmpty() {
        return count == 0;
    }
}
