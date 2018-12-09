package net.jcip.examples.shutdown;

import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

import net.jcip.annotations.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Using TrackingExecutorService to save unfinished tasks for later execution
 */
public abstract class WebCrawler {

    private static final long TIMEOUT = 500;
    private static final TimeUnit UNIT = MILLISECONDS;
    private volatile TrackingExecutor executor;
    private final ConcurrentMap<URL, Boolean> seen = new ConcurrentHashMap<>();
    @GuardedBy("this")
    private final Set<URL> urlsToCrawl = new HashSet<>();

    public WebCrawler(URL startUrl) {
        urlsToCrawl.add(startUrl);
    }

    public synchronized void start() {
        executor = new TrackingExecutor(Executors.newCachedThreadPool());
        for (URL url : urlsToCrawl) {
            submitCrawlTask(url);
        }
        urlsToCrawl.clear();
    }

    public synchronized void stop() throws InterruptedException {
        try {
            saveUncrawled(executor.shutdownNow());
            if (executor.awaitTermination(TIMEOUT, UNIT)) {
                saveUncrawled(executor.getCancelledTasks());
            }
        } finally {
            executor = null;
        }
    }

    protected abstract List<URL> processPage(URL url);

    private void saveUncrawled(List<Runnable> uncrawled) {
        for (Runnable task : uncrawled) {
            urlsToCrawl.add(((CrawlTask) task).getPage());
        }
    }

    private void submitCrawlTask(URL url) {
        executor.execute(new CrawlTask(url));
    }

    private class CrawlTask implements Runnable {
        private final URL url;
        private int count = 1;

        CrawlTask(URL url) {
            this.url = url;
        }

        boolean alreadyCrawled() {
            return seen.putIfAbsent(url, true) != null;
        }

        void markUncrawled() {
            seen.remove(url);
            System.out.printf("marking %s uncrawled%n", url);
        }

        public void run() {
            // будет вызван processPage() и вёрнёт все найденные другие урлы из него
            for (URL link : processPage(url)) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                // создадутся новые таски для каждого полученного урла
                submitCrawlTask(link);
            }
        }

        public URL getPage() {
            return url;
        }
    }
}
