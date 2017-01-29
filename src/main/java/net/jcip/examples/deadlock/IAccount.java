package net.jcip.examples.deadlock;

/**
 * Created by Denis on 23 Январь 2017
 */
interface IAccount {
    void debit(IDollarAmount d);

    void credit(IDollarAmount d);

    IDollarAmount getBalance();

    int getAcctNo();
}
