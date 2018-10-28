package by.den.constructor;

import java.util.ArrayList;

public class AnonymousArgs {
    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<String>(10) {
            @Override
            public int size() {
                return super.size();
            }
        };
    }

}
