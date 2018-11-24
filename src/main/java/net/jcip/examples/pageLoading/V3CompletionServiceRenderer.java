package net.jcip.examples.pageLoading;

import java.util.*;
import java.util.concurrent.*;
import static net.jcip.examples.LaunderThrowable.launderThrowable;

/**
 * Using CompletionService to render page elements as they become available
 */
public abstract class V3CompletionServiceRenderer implements BasicPageRenderer {
    private final CompletionService<ImageData> completionService;

    /**
     * @param executor a supplied Executor to execute tasks
     */
    V3CompletionServiceRenderer(ExecutorService executor) {
        // на одном executor может висеть много разных ExecutorCompletionService,
        // т.е. executor будет выполнять задачи, а источниками для него могут быть
        // разные CompletionService (и они будут засылать в него совсем разные задачи)
        // а он один (executor) будет их все выполнять.
        this.completionService = new ExecutorCompletionService<>(executor);
    }

    void renderPage(CharSequence source) {
        final List<ImageInfo> imagesList = scanForImageInfo(source);
        for (final ImageInfo imageInfo : imagesList) {
            completionService.submit(imageInfo::downloadImage);
        }
            /*completionService.submit( new Callable<ImageData>() {
                    public ImageData call() {
                        return imageInfo.downloadImage();
                    }
            });*/

        renderText(source);

        try {
            // Надо самостоятельно контролировать кол-во переданный и полученных результатов
            for (int t = 0, n = imagesList.size(); t < n; t++) {
                // Ждём когда появится любая завершившаяся задача, и берём её результат
                renderImage(completionService.take().get());
                /*Future<ImageData> f = completionService.take();
                ImageData imageData = f.get();
                renderImage(imageData);*/
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw launderThrowable(e.getCause());
        }
    }

}
