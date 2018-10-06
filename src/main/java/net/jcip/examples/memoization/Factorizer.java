package net.jcip.examples.memoization;

import java.math.BigInteger;
import javax.servlet.*;

import net.jcip.annotations.*;
import net.jcip.examples.memoization.Computable;
import net.jcip.examples.memoization.Memoizer;

/**
 * Factorizer
 * <p/>
 * Factorizing servlet that caches results using Memoizer
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class Factorizer extends GenericServlet implements Servlet {

    private final Computable<BigInteger, BigInteger[]> c = this::factor;
    private final Computable<BigInteger, BigInteger[]> cache = new Memoizer<>(c);

    public void service(ServletRequest req, ServletResponse resp) {
        try {
            BigInteger i = extractFromRequest(req);
            encodeIntoResponse(resp, cache.compute(i));
        } catch (InterruptedException e) {
            encodeError(resp, "factorization interrupted");
        }
    }

    void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {
    }

    void encodeError(ServletResponse resp, String errorString) {
    }

    BigInteger extractFromRequest(ServletRequest req) {
        return new BigInteger("7");
    }

    BigInteger[] factor(BigInteger i) {
        // Doesn't really factor
        return new BigInteger[]{i};
    }
}
