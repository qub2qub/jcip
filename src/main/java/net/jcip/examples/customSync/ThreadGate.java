package net.jcip.examples.customSync;

import net.jcip.annotations.*;

/**
 * Re-closeable gate using wait and notifyAll
 */
@ThreadSafe
public class ThreadGate {
    // CONDITION-PREDICATE: opened-since(n) (isOpen || generation>n)
    @GuardedBy("this") private boolean isOpen;
    // Поколение в котором прибыл поток.
    @GuardedBy("this") private int generation;


    public synchronized void close() {
        isOpen = false;
    }

    public synchronized void open() {
        // после открытия - всё предыдущее полколение должно смочь пройти через ворота
        ++generation;
        isOpen = true;
        notifyAll();
    }

    // BLOCKS-UNTIL: opened-since(generation on entry)
    public synchronized void await() throws InterruptedException {
        /*
        if N threads are waiting at the gate at the time it is opened, they should all be allowed to proceed.
        But, if the gate is opened and closed in rapid succession, all threads might not be released if await examines only isOpen:
        by the time all the threads receive the notification, reacquire the lock, and emerge from wait, the gate may have closed again.
        So: every time the gate is closed, a "generation" counter is incremented, and a thread may pass await if the gate is open now
        or if the gate has opened since this thread arrived at the gate.
         */
        int arrivalGeneration = generation;
        while (!isOpen && arrivalGeneration == generation) {
            wait();
        }
    }
}
