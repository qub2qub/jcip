package net.jcip.examples.jmm;

/**
 * PossibleReordering
 * <p/>
 * Insufficiently synchronized program that can have surprising results
 *
 * @author Brian Goetz and Tim Peierls
 */
public class PossibleReordering {
    static int x = 0, y = 0;
    static int a = 0, b = 0;

    private static void run() throws InterruptedException {
        Thread one = new Thread(new Runnable() {
            public void run() {
                a = 1;
                x = b;
            }
        });
        Thread other = new Thread(new Runnable() {
            public void run() {
                b = 1;
                y = a;
            }
        });
        one.start();
        other.start();
        one.join();
        other.join();
        System.out.println("( " + x + "," + y + ")");
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

}
