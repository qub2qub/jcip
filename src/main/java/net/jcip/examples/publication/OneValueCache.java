package net.jcip.examples.publication;

import java.math.BigInteger;
import java.util.*;

import net.jcip.annotations.*;

/**
 * OneValueCache
 * <p/>
 * Immutable holder for caching a number and its factors
 *
 * @author Brian Goetz and Tim Peierls
 */
@Immutable
public class OneValueCache {
    private final BigInteger lastNumber;
    private final BigInteger[] lastFactors;

    public OneValueCache(BigInteger i, BigInteger[] factors) {
        //<editor-fold desc="Constructor-Description">
        lastNumber = i;
        lastFactors = Arrays.copyOf(factors, factors.length);
        //</editor-fold>
    }

    public BigInteger[] getFactors(BigInteger i) {
        if (lastNumber == null || !lastNumber.equals(i))
            return null;
        else
            return Arrays.copyOf(lastFactors, lastFactors.length);
    }
}
