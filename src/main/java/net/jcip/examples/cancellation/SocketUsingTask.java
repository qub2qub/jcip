package net.jcip.examples.cancellation;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

import net.jcip.annotations.*;

/**
 * Encapsulating nonstandard cancellation in a task with newTaskFor
 */
public abstract class SocketUsingTask <T> implements CancellableTask<T> {
    @GuardedBy("this") private Socket socket;

    protected synchronized void setSocket(Socket s) {
        socket = s;
    }

    public synchronized void cancel() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ignored) {
        }
    }

    public RunnableFuture<T> newTask() {
        return new FutureTask<T>(this) {
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    // что за фигня?
                    // так можно взять ссылку на класс, в котором тебя создали.
                    // (для внутренних классов)
                    SocketUsingTask.this.cancel();
                    // Потому что просто this - будет ссылатьсся на анонимный класс  new FutureTask
                    // А чтобы получить this от SocketUsingTask (т.е. ссылку на интерфейс) --
                    // надо указать имя класса точка зис.
                } finally {
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }
}

interface CancellableTask <T> extends Callable<T> {
    void cancel(); // adds new method
    RunnableFuture<T> newTask(); // factory method
}

@ThreadSafe
class CancellingExecutor extends ThreadPoolExecutor {
    //<editor-fold desc="constructors">
    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }
    //</editor-fold>

    /**
     * overrides newTaskFor to let a CancellableTask create its own Future
     */
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        // Спец.штука -- если придёт наш таск -- то он создат и нашу кастомную фьючу.
        if (callable instanceof CancellableTask)
            return ((CancellableTask<T>) callable).newTask();
        else
            return super.newTaskFor(callable);
    }
}
