package net.jcip.examples.jmm;

import net.jcip.annotations.*;

/**
 * Lazy initialization holder class idiom
 */
@ThreadSafe
public class ResourceFactory {
    private static class ResourceHolder {
        public static Resource resource = new Resource();
    }
    public static Resource getResource() {
        return ResourceFactory.ResourceHolder.resource;
    }
    static class Resource {}
}
