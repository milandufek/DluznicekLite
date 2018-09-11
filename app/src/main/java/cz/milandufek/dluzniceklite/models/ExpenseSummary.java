package cz.milandufek.dluzniceklite.models;

import android.arch.lifecycle.ViewModel;

public class ExpenseSummary extends ViewModel {

    private String currencyName;
    private double sumSpent;
//    private MutableLiveData<String> currencyName;
//    private MutableLiveData<Double> sumSpent;

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
