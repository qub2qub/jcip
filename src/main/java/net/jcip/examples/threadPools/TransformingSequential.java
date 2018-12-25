package net.jcip.examples.threadPools;

import java.util.*;
import java.util.concurrent.*;

/**
 * Transforming sequential execution into parallel execution
 */
public abstract class TransformingSequential {

    void processSequentially(List<Element> elements) {
//        for (Element e : elements) {
//            process(e);
//        }
        elements.forEach(this::process);
    }

    void processInParallel(Executor exec, List<Element> elements) {
//        for (final Element e : elements) {
//            exec.execute(() -> process(e));
//        }
        elements.forEach(e -> exec.execute(() -> process(e)));
    }

    public abstract void process(Element e);

    public <T> void sequentialRecursive(List<Node<T>> nodes, Collection<T> results) {
        for (Node<T> n : nodes) {
            results.add(n.compute());
            sequentialRecursive(n.getChildren(), results);
        }
    }

    /**
     * Например чтобы пройти по всему дереву.
     */
    public <T> void parallelRecursive(final Executor executor, List<Node<T>> nodes, final Collection<T> results) {
        for (final Node<T> n : nodes) {
            executor.execute(() -> results.add(n.compute()));
            parallelRecursive(executor, n.getChildren(), results);
        }
    }

    public <T> Collection<T> getParallelResults(List<Node<T>> nodes) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        Queue<T> resultQueue = new ConcurrentLinkedQueue<>();
        parallelRecursive(executor, nodes, resultQueue);
        executor.shutdown();
        // очень долго ждёт шатдауна
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return resultQueue;
    }

    interface Element {
    }

    interface Node <T> {
        T compute();

        List<Node<T>> getChildren();
    }
}

