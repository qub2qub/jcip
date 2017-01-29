package net.jcip.examples.deadlock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Denis on 23 Январь 2017
 */
class Account implements IAccount {
    private IDollarAmount balance;
    private final int acctNo;
    private static final AtomicInteger sequence = new AtomicInteger();

    public Account(IDollarAmount balance) {
        this.balance = balance;
        acctNo = sequence.incrementAndGet();
    }

    @Override
    public void debit(IDollarAmount d) {
        balance = balance.subtract(d);
    }

    @Override
    public void credit(IDollarAmount d) {
        balance = balance.add(d);
    }

    @Override
    public IDollarAmount getBalance() {
        return balance;
    }

    @Override
    public int getAcctNo() {
        return acctNo;
    }

    @Override
    public String toString() {
        return "Acc{" + acctNo + '}';
    }
}
