package net.jcip.examples.pageLoading;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static net.jcip.examples.LaunderThrowable.launderThrowable;

/**
 * Waiting for image download with Future
 */
public abstract class V2FutureRenderer implements BasicPageRenderer {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    void renderPage(CharSequence source) {

        final List<ImageInfo> imageInfos = scanForImageInfo(source);
        // всё равно только 2 потока: 1й для текста и 2й для всех картинок
        Callable<List<ImageData>> downloadImageTask =
                () -> imageInfos.stream()
                        .map(ImageInfo::downloadImage)
                        .collect(Collectors.toList());
        /*new Callable<List<ImageData>>() {
            public List<ImageData> call() {
                List<ImageData> result = new ArrayList<>();
                for (ImageInfo imageInfo : imageInfos) {
                    result.add(imageInfo.downloadImage());
                }
                return result;
            }
        };*/
        // зпускаем загрузку картинок (хоть миллион потоков в этом случаев)
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

}
