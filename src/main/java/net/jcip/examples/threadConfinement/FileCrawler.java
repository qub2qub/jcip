package net.jcip.examples.threadConfinement;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

class FileCrawler implements Runnable {
    private final BlockingQueue<File> fileQueue;
    private final FileFilter fileFilter;
    private final File root;

    public FileCrawler(BlockingQueue<File> fileQueue, final FileFilter fileFilter, File root) {
        this.fileQueue = fileQueue;
        this.root = root;
        this.fileFilter = f -> f.isDirectory() || fileFilter.accept(f);
            /*new FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || fileFilter.accept(f);
                }
            };*/
    }

    @Override
    public void run() {
        try {
            crawl(root);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean alreadyIndexed(File f) {
        return false;
    }

    private void crawl(File root) throws InterruptedException {
        File[] entries = root.listFiles(fileFilter);
        if (entries != null) {
            for (File entry : entries) {
                if (entry.isDirectory()) {
                    crawl(entry);
                } else if (!alreadyIndexed(entry)) {
                    fileQueue.put(entry);
                }
            }
        }
    }

}
