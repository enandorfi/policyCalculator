package co.platform;

import java.util.Objects;


public class Quote {

    private int value;
    private int excess;
    private double price;

    Quote(int value, int excess) {
        this.value = value;
        this.excess = excess;
    }

    public Quote(int value, int excess, double price) {
        this.value = value;
        this.excess = excess;
        this.price = price;
    }

    protected void setPrice(double price) {
        this.price = price;
    }

    protected int getValue() {
        return this.value;
    }

    public int getExcess() {
        return excess;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quote)) return false;
        Quote quote = (Quote) o;
        return getValue() == quote.getValue() &&
                getExcess() == quote.getExcess();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getValue(), getExcess());
    }
}