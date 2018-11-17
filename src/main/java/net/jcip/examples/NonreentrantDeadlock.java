package net.jcip.examples;

/**
 * Potential NonreentrantDeadlock
 * Code that would deadlock if intrinsic locks were not reentrant
 */
class Widget {
    public synchronized void doSomething() {
    }
}

class LoggingWidget extends Widget {
    public synchronized void doSomething() {
        System.out.println(toString() + ": calling doSomething");
        super.doSomething();
    }
}
