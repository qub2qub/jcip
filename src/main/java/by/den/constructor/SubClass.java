package by.den.constructor;

public class SubClass extends SuperClass {
    final int number;
    public SubClass () {
        super();
        number = 2;
        //number = 3; // cannot assign to final for the 2nd time.
    }

    public synchronized void doSomethingDangerous() {
        if(number == 2) {
            System.out.println("everything OK");
        } else {
            System.out.println("we have a problem.");
        }
    }

    public static void main(String[] args) {
        int p = Runtime.getRuntime().availableProcessors();
        System.out.println("p = " + p);
        new SubClass();
    }
}
