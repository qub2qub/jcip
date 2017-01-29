package net.jcip.examples.deadlock;

/**
 * DynamicOrderDeadlock
 * <p/>
 * Dynamic lock-ordering deadlock
 *
 * @author Brian Goetz and Tim Peierls
 */
public class DynamicOrderDeadlock {
    // Warning: deadlock-prone!
    public static void transferMoney(Account fromAccount, Account toAccount, DollarAmount amount)
            throws InsufficientFundsException {

        synchronized (fromAccount) {
            synchronized (toAccount) {
//                System.out.print("fromAccount = " + fromAccount);
//                System.out.println(" balance = " + fromAccount.getBalance());
                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException();
                } else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                    System.out.print(".");
                }
            }
        }
    }

}
