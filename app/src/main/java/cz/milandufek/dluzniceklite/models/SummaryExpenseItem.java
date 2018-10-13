package cz.milandufek.dluzniceklite.models;

public final class SummaryExpenseItem {

    private int currencyId;
    private String currencyName;
    private double sumSpent;

    public SummaryExpenseItem() {
    }

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
}