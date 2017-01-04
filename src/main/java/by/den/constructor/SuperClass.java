package by.den.constructor;

public abstract class SuperClass {

    public SuperClass() {
        Thread thread = new Thread("evil") {
            public void run() {
                doSomethingDangerous();
            }
        };
        thread.start();
        try {
            Thread.sleep(5000);
        }
        catch(InterruptedException ex) { /* ignore */ }
    }

    public abstract void doSomethingDangerous();

}
