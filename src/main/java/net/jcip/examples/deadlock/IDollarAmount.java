package net.jcip.examples.deadlock;

/**
 * Created by Denis on 23 Январь 2017
 */
interface IDollarAmount extends Comparable<IDollarAmount> {
    int getAmount();
    void setAmount(int amount);
    IDollarAmount add(IDollarAmount d);
    IDollarAmount subtract(IDollarAmount d);
}
