package cz.milandufek.dluzniceklite.models;

import java.util.Observable;
import java.util.Observer;

public final class SummaryExpenseItem implements Observer {

    private int currencyId;
    private String currencyName;
    private double sumSpent;

    public SummaryExpenseItem(int currencyId, String currencyName, double sumSpent) {
        this.currencyId = currencyId;
        this.currencyName = currencyName;
        this.sumSpent = sumSpent;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public double getSumSpent() {
        return sumSpent;
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO
    }

    @Override
    public String toString() {
        return SummaryExpenseItem.class.toString() + " = { " +
                "currencyId = " + currencyId +
                ", currencyName = " + currencyName +
                ", sumSpent = " + sumSpent +
                " }";
    }
}
