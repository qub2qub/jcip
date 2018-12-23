package net.jcip.examples.threadPools;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Task that deadlocks in a single-threaded Executor.
 * Illustrates thread starvation deadlock.
 * With a single-threaded executor, it will always deadlock.
 */
public class ThreadDeadlock {
    ExecutorService executor = Executors.newSingleThreadExecutor();
//    ExecutorService executor = Executors.newCachedThreadPool();

    public class LoadFileTask implements Callable<String> {
        private final String fileName;

        public LoadFileTask(String fileName) {
            this.fileName = fileName;
        }

        public String call() throws Exception {
            System.out.println("LoadFileTask -- fileName = " + fileName);
            // Here's where we would actually read the file
            return "{" + fileName + "}";
        }
    }

    public class RenderPageTask implements Callable<String> {
        public String call() throws Exception {
            Future<String> header, footer;
            System.out.println("1");
            header = executor.submit(new LoadFileTask("header.html"));
            System.out.println("2");
            footer = executor.submit(new LoadFileTask("footer.html"));
            System.out.println("3");
            String page = renderBody();
            System.out.println("4");
            // Will deadlock -- task waiting for result of subtask
            // потому что этот RenderPageTask уже занял 1 поток, и ждёт резултатов
            // выполнения 2х подзадач, который в SingleThreadExecutor стоят в очереди после него.
            return header.get() + page + footer.get();
        }

        private String renderBody() {
            System.out.println("renderBody");
            // Here's where we would actually render the page
            return "{renderBody}";
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        ThreadDeadlock threadDeadlock = new ThreadDeadlock();
        RenderPageTask renderPageTask = threadDeadlock.new RenderPageTask();
        // вызовем всё в 1 executor -- и будет дэдлок
//        Future<String> future = threadDeadlock.executor.submit(renderPageTask);
        // если основную задачу запустим в другом потоке -- то всё будет ОК
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(renderPageTask);
        String result;
        try {
            result = future.get(2, TimeUnit.SECONDS);
            System.out.println("result = " + result);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.out.println("Exception while getting results: " + e.getMessage());
        } finally {
            System.out.println("main shutdown");
            threadDeadlock.executor.shutdownNow();
            executorService.shutdownNow();
        }
    }
}
