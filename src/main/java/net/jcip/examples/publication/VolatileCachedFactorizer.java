package net.jcip.examples.publication;

import java.math.BigInteger;
import javax.servlet.*;

import net.jcip.annotations.*;

/**
 * Caching the last result using a volatile reference to an immutable holder object
 */
@ThreadSafe
public class VolatileCachedFactorizer extends GenericServlet implements Servlet {
    private volatile OneValueCache cache = new OneValueCache(null, null);

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = cache.getFactors(i);
        if (factors == null) {
            factors = factor(i);
            // заменить immutable obj другим, а чтобы другие потоки его увидели-
            // он сделан volatile
            cache = new OneValueCache(i, factors); // immutable class
        }
        encodeIntoResponse(resp, factors);
    }

    void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {
    }

    BigInteger extractFromRequest(ServletRequest req) {
        return new BigInteger("7");
    }

    BigInteger[] factor(BigInteger i) {
        // Doesn't really factor
        return new BigInteger[]{i};
    }
}

