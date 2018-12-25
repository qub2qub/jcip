package net.jcip.examples.shutdown;

/**
 * Registering a shutdown hook to stop the logging service.
 */
public class AddShutdownHook {

    public void start() {
        // 1) use a SINGLE SHUTDOWN HOOK FOR ALL SERVICES, rather than one for each service;
        // 2) maintain explicit dependency information among services, this technique can also ensure
        // that shutdown actions are performed in the right order.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    //LogService.this.stop();
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                    // ignore
                }
            }
        });
    }
}
