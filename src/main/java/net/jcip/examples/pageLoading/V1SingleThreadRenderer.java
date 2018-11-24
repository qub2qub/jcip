package net.jcip.examples.pageLoading;

import java.util.ArrayList;
import java.util.List;

/**
 * Rendering page elements sequentially
 */
public abstract class V1SingleThreadRenderer implements BasicPageRenderer {

    void renderPage(CharSequence source) {
        renderText(source);

        List<ImageData> imageData = new ArrayList<>();
        for (ImageInfo imageInfo : scanForImageInfo(source)) {
            imageData.add(imageInfo.downloadImage());
        }

        for (ImageData data : imageData) {
            renderImage(data);
        }
    }

}
