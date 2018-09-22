package cz.milandufek.dluzniceklite.models;

public final class Expense {

    private int id, payerId, groupId, currencyId;
    private String reason, date, time;

    public Expense(int id, int payerId, int groupId, int currencyId, String reason, String date, String time) {
        this.id = id;
        this.payerId = payerId;
        this.groupId = groupId;
        this.currencyId = currencyId;
        this.reason = reason;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getPayerId() {
        return payerId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getReason() {
        return reason;
    }
}
