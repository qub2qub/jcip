package net.jcip.examples.customSync;

import java.util.Random;
import java.util.concurrent.locks.*;

import net.jcip.annotations.*;

/**
 * OneShotLatch
 * <p/>
 * Binary latch using AbstractQueuedSynchronizer
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class OneShotLatch {
    private final Sync sync = new Sync();

    public void signal() {
        System.out.println("__ latch.signal __");
        // вызывает tryReleaseShared(), и в него же передаётся аргумент
        sync.releaseShared(0);
    }

    public void await() throws InterruptedException {
        // вызывает tryAcquireShared(), и в него же передаётся аргумент
        sync.acquireSharedInterruptibly(0);
    }

    public void printSyncState() {
        System.out.println("................................." +
                "state="+sync.getSyncState() + ", Q="+sync.getQueueLength());
    }

    public void awaitExclusively() throws InterruptedException {
        // вызывает tryAcquireShared(), и в него же передаётся аргумент
        sync.acquireInterruptibly(0);
    }
    public void releaseExclusively() {
        System.out.println("__  sync.release __");
        // вызывает tryReleaseShared(), и в него же передаётся аргумент
        sync.release(0); // и этим вызовом как бы освообждает лок/акваэ
    }

    /**
     * private невидимый класс
     */
    private class Sync extends AbstractQueuedSynchronizer {
        /**
         * a negative value -- indicates acquisition failure;<p>
         * zero indicates the synchronizer was acquired exclusively;<p>
         * a positive value indicates the synchronizer was acquired nonexclusively.<p>
         */
        @Override
        protected int tryAcquireShared(int ignored) {
            // Succeed if latch is open (state == 1), else fail
            return (getState() == 1) ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int ignored) {
            setState(1); // Latch is now open
            return true; // Other threads may now be able to acquire

        }

        @Override
        protected boolean tryAcquire(int arg) {
            // если гейт открыт -- то Acquire будет успешным,
            // т.е. никто не будет ждать, а сразу пойдёт выполнение,
            // но т.к. тут EXCLUSIVE ACQUISITION -- то только 1 сможет получить лок/акваэ
            return (getState() == 1);
        }

        @Override
        protected boolean tryRelease(int arg) {
            setState(1);
            return true;
        }

        protected final int getSyncState() {
            return getState();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        OneShotLatch latch = new OneShotLatch();
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            //----------------------------------------
            Thread thread = new Thread("Thread#"+i) {
                @Override
                public void run() {
                    try {
                        System.out.println(getName() + " created.");
//                        latch.await();
                        latch.awaitExclusively();
                        long rnd = random.nextInt(3000);
                        System.out.println(getName() + " working....."+rnd);
                        Thread.sleep(500+rnd);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println(getName() + " completed!");
                        latch.printSyncState();
                        latch.releaseExclusively(); // не нужно если вызывается tryAcquireShared()
                        // тогда достаточно 1 сигнала, чтобы отпустить все потоки
                        // этот 1 сигнал "latch.signal()" будет из main ниже.
                        // !!! при этом лок уже открыт, только нужно послать ивент,
                        // !!! чтобы новый поток получил сигнал и начал выполняться
                    }
                }
            };
            //----------------------------------------
            thread.start();
        }
        Thread.sleep(2000);
        System.out.println("==================================");

        latch.signal();
//        latch.releaseExclusively();
    }

}
