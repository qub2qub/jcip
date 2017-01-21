import java.math.BigInteger;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("availableProcessors = " + availableProcessors);
        BigInteger bigInteger =  BigInteger.ONE;
        while (true)
            System.out.print(".");
//            bigInteger = bigInteger.nextProbablePrime();
    }
}
