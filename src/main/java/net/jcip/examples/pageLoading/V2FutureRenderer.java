package net.jcip.examples.pageLoading;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static net.jcip.examples.LaunderThrowable.launderThrowable;

/**
 * Waiting for image download with Future.
 *
 * FutureRenderer uses two tasks: one for rendering text and one for downloading the images.
 * If rendering the text is much faster than downloading the images, as is entirely possible,
 * the resulting performance is not much different from the sequential version, but the code is a lot more complicated.
 * And the best we can do with two threads is speed things up by a factor of two.
 * Thus, trying to increase concurrency by parallelizing heterogeneous activities can be a lot of work,
 * and there is a limit to how much additional concurrency you can get out of it.
 */
public abstract class V2FutureRenderer implements BasicPageRenderer {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    void renderPage(CharSequence source) {

        final List<ImageInfo> imageInfos = scanForImageInfo(source);
        // Пример как можно создать Callable, и 3й способ - inline in submit().
        /*
        Callable<List<ImageData>> downloadImageTask =
                () -> imageInfos.stream()
                        .map(ImageInfo::downloadImage)
                        .collect(Collectors.toList());
        new Callable<List<ImageData>>() {
            public List<ImageData> call() {
                List<ImageData> result = new ArrayList<>();
                for (ImageInfo imageInfo : imageInfos) {
                    result.add(imageInfo.downloadImage());
                }
                return result;
            }
        };
        Future<List<ImageData>> future = executor.submit(downloadImageTask);
        */

        // всё равно только 2 потока: 1й для всех картинок и 2й для текста
        // 1) зпускаем отдельно загрузку картинок (хоть миллион потоков в этом случаев)
        Future<List<ImageData>> downloadImageFuture = executor.submit(() ->
                imageInfos.stream()
                .map(ImageInfo::downloadImage)
                .collect(Collectors.toList()));

        // 2) отрисовываем текст на странице
        renderText(source);

        try {
            // блокирует текущий поток пока все картинки не загрузяся
            List<ImageData> imageData = downloadImageFuture.get();
            // отрисовываем картинки на странице
            for (ImageData data : imageData) {
                renderImage(data);
            }
        } catch (InterruptedException e) {
            // Re-assert the thread's interrupted status
            Thread.currentThread().interrupt();
            // We don't need the result, so cancel the downloadImageTask too
            downloadImageFuture.cancel(true);
        } catch (ExecutionException e) {
            throw launderThrowable(e.getCause());
        }
    }

}
