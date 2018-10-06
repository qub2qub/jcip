package net.jcip.examples;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Preloader
 * <p>
 * Using FutureTask to preload data that is needed later
 *
 * @author Brian Goetz and Tim Peierls
 */

public class Preloader {

    ProductInfo loadProductInfo() throws DataLoadException, InterruptedException {
        // долгая задача, которая начётся заранее
        System.out.println("Start long computation...");
        Thread.sleep(5000);
        System.out.println("Finish computation...");
        return () -> "RESULT";
    }

    private final FutureTask<ProductInfo> future = new FutureTask<>(this::loadProductInfo);
    //    private final FutureTask<ProductInfo> future = new FutureTask<>(() -> loadProductInfo());
    /*private final FutureTask<ProductInfo> future =
            new FutureTask<ProductInfo>(new Callable<ProductInfo>() {
                public ProductInfo call() throws DataLoadException {
                    return loadProductInfo();
                }
            });*/
    private final Thread thread = new Thread(future);

    public void start() {
        thread.start();
    } // запустить долгое вычисление

    public ProductInfo get() throws DataLoadException, InterruptedException {
        try {
            return future.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof DataLoadException)
                throw (DataLoadException) cause;
            else
                throw LaunderThrowable.launderThrowable(cause);
        }
    }

    interface ProductInfo {
        String getName();
    }

    public static void main(String[] args) throws InterruptedException, DataLoadException {
        Preloader loader = new Preloader();
        loader.start();
        System.out.println("Get the result... in main thread ...");
        ProductInfo productInfo = loader.get();
        System.out.println("productInfo = " + productInfo.getName());
    }
}

class DataLoadException extends Exception {
}
