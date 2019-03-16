package net.jcip.examples.customSync;

import net.jcip.annotations.*;

/**
 * Bounded buffer that balks when preconditions are not met.
 * Propagating precondition failure to callers.
 */
@ThreadSafe
public class One_GrumpyBoundedBuffer<V> extends BaseBoundedBuffer<V> {
    public One_GrumpyBoundedBuffer() {
        this(100);
    }

    public One_GrumpyBoundedBuffer(int size) {
        super(size);
    }

    public synchronized void put(V v) throws BufferFullException {
        if (isFull()) {
            throw new BufferFullException();
        }
        doPut(v);
    }

    public synchronized V take() throws BufferEmptyException {
        if (isEmpty()) {
            throw new BufferEmptyException();
        }
        return doTake();
    }
}

class ExampleUsage {
    private One_GrumpyBoundedBuffer<String> buffer;
    int SLEEP_GRANULARITY = 50;

    void useBuffer() throws InterruptedException {
        // надо обрабатывать Exception
        while (true) {
            try {
                String item = buffer.take();
                // use item
                break;
            } catch (BufferEmptyException e) {
                Thread.sleep(SLEEP_GRANULARITY);
            }
        }
    }
}

class BufferFullException extends RuntimeException {
}

class BufferEmptyException extends RuntimeException {
}
