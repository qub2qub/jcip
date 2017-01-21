package by.den.nestedClasses;

public class ShadowTest {
    public int x = 0;

    class FirstLevel {
        public int x = 1;
        void methodInFirstLevel(int x) {
            System.out.println("x = " + x + " // значение из аргумента метода");
            System.out.println("this.x = " + this.x + " // значение из внутреннего класса");
            System.out.println("ShadowTest.this.x = " + ShadowTest.this.x + " // значение из внешнего класса");
        }
    }

    public static void main(String... args) {
        ShadowTest shadowTest = new ShadowTest();
        ShadowTest.FirstLevel firstLevel = shadowTest.new FirstLevel();
        firstLevel.methodInFirstLevel(23);
    }
}