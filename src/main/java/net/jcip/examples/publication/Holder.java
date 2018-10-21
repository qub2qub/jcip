package net.jcip.examples.publication;

/**
 * Holder
 * <p/>
 * Class at risk of failure if not properly published
 *
 * @author Brian Goetz and Tim Peierls
 */
public class Holder {
    private int n;

    public Holder(int n) {
        this.n = n;
        // assertSanity(); // escape this and call in another thread
    }

    public void assertSanity() {
        if (n != n)
            throw new AssertionError("This statement is false.");
    }

    @Override
    public String toString() {
        return "Holder{" +
                "n=" + n +
                '}';
    }
}
