package net.jcip.examples;

/**
 * ThisEscape
 * <p/>
 * Implicitly allowing the this reference to escape
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ThisEscape {
    public ThisEscape(EventSource source) {
        source.registerListener(this::doSomething);
    }

    private ThisEscape() { } // private constructor
    public static ThisEscape createSafeEscape(EventSource source) {
        ThisEscape thisEscape = new ThisEscape();
        source.registerListener(thisEscape::doSomething);
        return thisEscape;
    }

    void doSomething(Event e) { }
    interface EventSource { void registerListener(EventListener e);}
    interface EventListener { void onEvent(Event e);}
    interface Event { }
}

