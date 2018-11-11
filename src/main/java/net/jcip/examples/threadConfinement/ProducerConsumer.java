package net.jcip.examples.threadConfinement;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.*;

/**
 * Producer and consumer tasks in a desktop search application
 */
public class ProducerConsumer {

    private static final int BOUND = 10;
    private static final int N_CONSUMERS = Runtime.getRuntime().availableProcessors();

    public static void startIndexing(File[] roots) {
        BlockingQueue<File> queue = new LinkedBlockingQueue<>(BOUND);
        FileFilter filter = file -> true;
        /*new FileFilter() {
            public boolean accept(File file) {
                return true;
            }
        };*/

        for (File root : roots) {
            new Thread(new FileCrawler(queue, filter, root)).start();
        }

        for (int i = 0; i < N_CONSUMERS; i++) {
            new Thread(new Indexer(queue)).start();
        }
    }
}
