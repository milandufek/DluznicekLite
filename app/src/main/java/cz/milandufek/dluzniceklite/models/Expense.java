package cz.milandufek.dluzniceklite.models;

public class Expense {
    private static final String TAG = "Expense";

    private int id, payerId, groupId, currencyId;
    private String reason, date, time;

    public Expense() {
    }

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

    public void setId(int id) {
        this.id = id;
    }

    public int getPayerId() {
        return payerId;
    }

    public void setPayerId(int payerId) {
        this.payerId = payerId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
