package net.jcip.examples;

import java.util.*;

/**
 * Compound actions on a Vector that may produce confusing results
 */
public class UnsafeVectorHelpers {

    // Одновременные изменения могут налжиться и сломать
    // поэтому каждый должен синхонизироваться по самой коллекции.
    public static Object getLast(Vector list) {
        int lastIndex = list.size() - 1;
        return list.get(lastIndex);
    }

    public static void deleteLast(Vector list) {
        int lastIndex = list.size() - 1;
        list.remove(lastIndex);
    }
}
