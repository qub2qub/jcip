package net.jcip.examples.pageLoading;

import java.util.List;

public interface BasicPageRenderer {
    void renderText(CharSequence s);
    List<ImageInfo> scanForImageInfo(CharSequence s);
    void renderImage(ImageData i);
}
