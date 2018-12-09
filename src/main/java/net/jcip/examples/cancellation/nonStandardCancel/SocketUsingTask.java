package net.jcip.examples.cancellation.nonStandardCancel;

import net.jcip.annotations.GuardedBy;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Encapsulating nonstandard cancellation in a task with newTaskFor
 */
public class SocketUsingTask<T> implements CancellableTask<T> {
    @GuardedBy("this")
    private Socket socket;

    protected synchronized void setSocket(Socket s) {
        socket = s;
    }

    @Override
    public synchronized void cancel() {
        // custom cancellation code can perform logging or gather statistics on cancellation,
        // AND can also be used to cancel activities that are not responsive to interruption.
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
            // do nothing
        }
    }

    @Override
    public RunnableFuture<T> newTask() {
        return new FutureTask<T>(this) {
            /*
            If a SocketUsingTask is cancelled through its Future, the socket is closed
            and the executing thread is interrupted.
            This increases the task's responsiveness to cancellation:
            not only can it safely call interruptible blocking methods while remaining responsive to cancellation,
            but it can also call blocking socket I/O methods.
             */
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    // так можно взять ссылку на класс, в котором тебя создали. (для внутренних классов)
                    // чтобы закрыть сокет => вызывает кастомный синхронизированный метод
                    // .cancel() из SocketUsingTask
                    SocketUsingTask.this.cancel();
                    // Потому что просто this - будет ссылатьсся на анонимный класс  new FutureTask
                    // А чтобы получить this от SocketUsingTask (т.е. ссылку на интерфейс) --
                    // надо указать имя класса точка зис.
                } finally {
                    // FutureTask.cancel(bool) - чтобы прервать/отменить саму future
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }

    @Override
    public T call() throws Exception {
        // do smt with socket and future task
        return null;
    }

    public static void main(String[] args) {
        CancellingExecutor executor = new CancellingExecutor(
                1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        executor.submit(new SocketUsingTask<>());
    }
}




