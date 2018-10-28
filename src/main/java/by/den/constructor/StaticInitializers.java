package by.den.constructor;

public class StaticInitializers {

    static {
        Integer.valueOf("abc");
    }

    public static void main(String[] args) {
        StaticInitializers init = new StaticInitializers();
        System.out.println("init = " + init);
    }
}
