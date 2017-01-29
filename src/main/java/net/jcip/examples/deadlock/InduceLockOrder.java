package net.jcip.examples.deadlock;

/**
 * InduceLockOrder
 *
 * Inducing a lock order to avoid deadlock
 *
 * @author Brian Goetz and Tim Peierls
 */
public class InduceLockOrder {
    private final Object tieLock = new Object(); // "tie breaking" lock

    public void transferMoney(final IAccount fromAcct, final IAccount toAcct, final IDollarAmount amount)
            throws InsufficientFundsException {

        class Helper {
            public void transfer() throws InsufficientFundsException {
                if (fromAcct.getBalance().compareTo(amount) < 0)
                    throw new InsufficientFundsException();
                else {
                    fromAcct.debit(amount);
                    toAcct.credit(amount);
                    System.out.print(".");
                }
            }
        }

        int fromHash = System.identityHashCode(fromAcct);
        int toHash = System.identityHashCode(toAcct);

        if (fromHash < toHash) {
            synchronized (fromAcct) {
                synchronized (toAcct) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAcct) {
                synchronized (fromAcct) {
                    new Helper().transfer();
                }
            }
        } else {
            synchronized (tieLock) {
                synchronized (fromAcct) {
                    synchronized (toAcct) {
                        new Helper().transfer();
                    }
                }
            }
        }
    }

}
