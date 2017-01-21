package net.jcip.examples.threadPools;

import java.util.concurrent.*;

/**
 * ThreadDeadlock
 * <p/>
 * Task that deadlocks in a single-threaded Executor
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ThreadDeadlock {
    ExecutorService exec = Executors.newSingleThreadExecutor();
//    ExecutorService exec = Executors.newCachedThreadPool();

    public class LoadFileTask implements Callable<String> {
        private final String fileName;

        public LoadFileTask(String fileName) {
            this.fileName = fileName;
        }

        public String call() throws Exception {
            System.out.println("LoadFileTask -- fileName = " + fileName);
            // Here's where we would actually read the file
            return "";
        }
    }

    public class RenderPageTask implements Callable<String> {
        public String call() throws Exception {
            Future<String> header, footer;
            System.out.println("1");
            header = exec.submit(new LoadFileTask("header.html"));
            System.out.println("2");
            footer = exec.submit(new LoadFileTask("footer.html"));
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
            return "";
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        ThreadDeadlock threadDeadlock = new ThreadDeadlock();
        RenderPageTask renderPageTask = threadDeadlock.new RenderPageTask();
//        Future<String> future = threadDeadlock.exec.submit(renderPageTask);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(renderPageTask);
        System.out.println("res="+future.get(5, TimeUnit.SECONDS));
        System.out.println("main shutdown");
        threadDeadlock.exec.shutdown();
        executorService.shutdown();
    }
}
