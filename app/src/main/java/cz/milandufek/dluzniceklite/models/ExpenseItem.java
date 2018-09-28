package cz.milandufek.dluzniceklite.models;

public final class ExpenseItem {

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

    public String getReason() {
        return reason;
    }

    public String getPayer() {
        return payer;
    }

    public String getDebtors() {
        return debtors;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "id=" + id + " reason=" + reason + " payer=" + payer + " currency=" + currency +
            " debtors=" + debtors + " amount=" + amount + " date=" + date + " time=" + time;
    }
}
