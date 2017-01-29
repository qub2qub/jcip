package net.jcip.examples.deadlock;

/**
 * Created by Denis on 23 Январь 2017
 */
class DollarAmount implements IDollarAmount {
    // Needs implementation
    private int amount;

    public DollarAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public IDollarAmount add(IDollarAmount d) {
        amount += d.getAmount();
        return this;
    }

    @Override
    public IDollarAmount subtract(IDollarAmount d) {
        amount -= d.getAmount();
        return this;
    }

    @Override
    public int compareTo(IDollarAmount dollarAmount) {
        return Integer.compare(amount, dollarAmount.getAmount());
    }

    @Override
    public String toString() {
        return "Amount{" + amount + '}';
    }
}
