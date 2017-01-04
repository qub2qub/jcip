package by.den.solid;

/**
 * Created by Denis on 16 Декабрь 2016
 */
public class ParkingOnHighWay implements HighWay, ParkingLot{

    @Override
    public int getSpeed() {
        return 25;
    }

    @Override
    public boolean isTaxPayed() {
        return true;
    }

    @Override
    public int getLength() {
        return 4;
    }

    public static void main(String[] args) {
        String boss = "boss";
        char[] array = boss.toCharArray();

        for(char c : array)
        {
            if (c== 'o')
                c = 'a';
        }
        System.out.println(new String(array));
    }
}
