package net.jcip.examples.explicitLocks;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Locking with a time budget
 */
public class TimedLocking {
    private Lock lock = new ReentrantLock();

    /**
     * т.е. вызвав этот метод -- кто-то пытается послать сообщение
     * если неудалось - то ему в ответ придёт фолсе, и он будет знать,
     * что сообщение не отправилось.
     */
    public boolean trySendOnSharedLine(String message, long timeout, TimeUnit unit) throws InterruptedException {

        long nanosToLock = unit.toNanos(timeout) - estimatedNanosToSend(message);

        if (!lock.tryLock(nanosToLock, NANOSECONDS)) {
            return false;
        }
        // в этом месте лок уже получен
        try {
            return sendOnSharedLine(message);
        } finally {
            lock.unlock();
        }
    }

    private boolean sendOnSharedLine(String message) {
        /* send something */
        return true;
    }

    long estimatedNanosToSend(String message) {
        return message.length();
    }
}
