package net.jcip.examples.threadPools.my2;

import net.jcip.examples.threadPools.TimingThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class My2ScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    private volatile My2ThreadFactory factory;

    public My2ScheduledThreadPoolExecutor(int corePoolSize, My2ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
        this.factory = threadFactory;
    }

    public int getCreated() {
        return factory.getCreated();
    }

    public int getAlive() {
        return factory.getAlive();
    }

    public int getUncaught() {
        return factory.getUncaught();
    }

    @Override
    public String toString() {
        return "Executor{" +
                "Created=" + getCreated() +
                ", Alive=" + getAlive() +
                ", Uncaught=" + getUncaught() +
                '}';
    }

    public static void main(String[] args) {
        // It creates pool threads that have the same permissions, AccessControlContext, and contextClassLoader
        // as the thread creating the privilegedThreadFactory.
        ThreadFactory privilegedThreadFactory = Executors.privilegedThreadFactory();

        My2ThreadFactory myFactory = new My2ThreadFactory("MyFactory");
        My2ScheduledThreadPoolExecutor executor1 = new My2ScheduledThreadPoolExecutor(1, myFactory);
        ExecutorService executor2 = Executors.newFixedThreadPool(2, myFactory);
        ExecutorService executor = new TimingThreadPool();
        executor.execute(() -> {
            while (true) {
                System.out.println("1");
                System.out.println("2");
                System.out.println("3");
                throw new RuntimeException("ABC");
            }
        });
        System.out.println("myFactory = " + myFactory);
        System.out.println("executor 1 = " + executor);
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("executor 2 = " + executor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        executor.shutdownNow();
    }
}
