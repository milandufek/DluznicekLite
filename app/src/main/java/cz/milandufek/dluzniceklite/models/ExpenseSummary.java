package cz.milandufek.dluzniceklite.models;

public class ExpenseSummary {

    private String currencyName;
    private double sumSpent;

    public ExpenseSummary() {
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public double getSumSpent() {
        return sumSpent;
    }

    public void setSumSpent(double sumSpent) {
        this.sumSpent = sumSpent;
    }
}
