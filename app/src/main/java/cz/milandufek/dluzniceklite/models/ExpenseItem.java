package cz.milandufek.dluzniceklite.models;

public class ExpenseItem {

    private int id;
    private String reason, payer, currency;
    private String debtors;
    private double amount;
    private String date, time;

    public ExpenseItem(int id, String reason, String payer, String currency, String debtors, double amount, String date, String time) {
        this.id = id;
        this.reason = reason;
        this.payer = payer;
        this.currency = currency;
        this.debtors = debtors;
        this.amount = amount;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getDebtors() {
        return debtors;
    }

    public void setDebtors(String debtors) {
        this.debtors = debtors;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
