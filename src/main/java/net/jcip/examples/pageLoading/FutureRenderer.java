package net.jcip.examples.pageLoading;

import java.util.*;
import java.util.concurrent.*;
import static net.jcip.examples.LaunderThrowable.launderThrowable;

/**
 * Waiting for image download with \Future
 */
public abstract class FutureRenderer {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    void renderPage(CharSequence source) {

        final List<ImageInfo> imageInfos = scanForImageInfo(source);
        Callable<List<ImageData>> downloadImageTask =
                new Callable<List<ImageData>>() {
                    public List<ImageData> call() {
                        List<ImageData> result = new ArrayList<ImageData>();
                        for (ImageInfo imageInfo : imageInfos) {
                            result.add(imageInfo.downloadImage());
                        }
                        return result;
                    }
                };

        // зпускаем загрузку картинок
        Future<List<ImageData>> future = executor.submit(downloadImageTask);
        // отрисовываем текст на странице
        renderText(source);

        try {
            // блокирует тек.поток пока все картинки не загрузяся
            List<ImageData> imageData = future.get();
            // отрисовываем картинки на странице
            for (ImageData data : imageData) {
                renderImage(data);
            }
        } catch (InterruptedException e) {
            // Re-assert the thread's interrupted status
            Thread.currentThread().interrupt();
            // We don't need the result, so cancel the downloadImageTask too
            future.cancel(true);
        } catch (ExecutionException e) {
            throw launderThrowable(e.getCause());
        }
    }

    interface ImageData {
    }

    interface ImageInfo {
        ImageData downloadImage();
    }

    abstract void renderText(CharSequence s);

    abstract List<ImageInfo> scanForImageInfo(CharSequence s);

    abstract void renderImage(ImageData i);
}
