package net.jcip.examples.gui;

import java.util.concurrent.*;

/**
 * BackgroundTask
 * <p/>
 * Background task class supporting cancellation, completion notification, and progress notification
 *
 * @author Brian Goetz and Tim Peierls
 */

public abstract class BackgroundTask <V> implements Runnable, Future<V> {
    private final FutureTask<V> computation = new Computation();

    private class Computation extends FutureTask<V> {
        public Computation() {
            super(
                    // создаётся новый колабл у которого в коле
                    // выполняется метод компьют() из родителя.
                    new Callable<V>() {
                public V call() throws Exception {
                    return BackgroundTask.this.compute();
                }
            });
        }

        protected final void done() {
            // по завершении, добавляет ивэнт в ивэнт-срэд
            GuiExecutor.instance().execute(new Runnable() {
                public void run() {
                    V value = null;
                    Throwable thrown = null;
                    boolean cancelled = false;
                    try {
                        value = get();
                    } catch (ExecutionException e) {
                        thrown = e.getCause();
                    } catch (CancellationException e) {
                        cancelled = true;
                    } catch (InterruptedException consumed) {
                    } finally {
                        onCompletion(value, thrown, cancelled);
                    }
                };
            });
        }
    }

    // Called in the background thread (например в методе compute() )
    protected void setProgress(final int current, final int max) {
        GuiExecutor.instance().execute(new Runnable() {
            public void run() {
                onProgress(current, max);
            }
        });
    }

    // Called in the background thread (из FutureTask call() )
    protected abstract V compute() throws Exception;

    // Called in the event thread (по завершении, из done() )
    protected void onCompletion(V result, Throwable exception, boolean cancelled) {
        // GUI show popup and remove progress bar
    }

    // Called in the event thread  (чтобы где-то обновить/отрисовать прогесс)
    protected void onProgress(int current, int max) {
        // GUI update progress bar..
    }

    // Other Future methods just forwarded to computation
    public boolean cancel(boolean mayInterruptIfRunning) {
        return computation.cancel(mayInterruptIfRunning);
    }

    public V get() throws InterruptedException, ExecutionException {
        return computation.get();
    }

    /**
     * вызывает future.get(с таймаутом), и сам блокируется, ожидая завершения вычислений.
     */
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return computation.get(timeout, unit);
    }

    public boolean isCancelled() {
        return computation.isCancelled();
    }

    public boolean isDone() {
        return computation.isDone();
    }

    public void run() {
        computation.run();
    }
}
