package net.jcip.examples.jmm;

import net.jcip.annotations.*;

/**
 * Thread-safe lazy initialization
 */
@ThreadSafe
public class SafeLazyInitialization {
    private static Resource resource;

    public synchronized static Resource getInstance() {
        if (resource == null)
            resource = new Resource();
        return resource;
    }

    static class Resource {}
}
