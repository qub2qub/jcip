package net.jcip.examples.cancellation.nonStandardCancel;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

interface CancellableTask <T> extends Callable<T> {
    void cancel(); // adds new method
    RunnableFuture<T> newTask(); // factory method
}
