package cz.milandufek.dluzniceklite.models;

import android.arch.lifecycle.ViewModel;

public class SummaryExpense extends ViewModel {

    private int currencyId;
    private String currencyName;
    private double sumSpent;

    public SummaryExpense() {
    }

    public SummaryExpense(int currencyId, String currencyName, double sumSpent) {
        this.currencyId = currencyId;
        this.currencyName = currencyName;
        this.sumSpent = sumSpent;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
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
