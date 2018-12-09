package net.jcip.examples.shutdown;

/**
 * Registering a shutdown hook to stop the logging service.
 */
public class AddShutdownHook {

    public void start() {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    //LogService.this.stop();
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        });

    }
}
