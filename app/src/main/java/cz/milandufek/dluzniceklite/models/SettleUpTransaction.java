package cz.milandufek.dluzniceklite.models;

public final class SettleUpTransaction {

    private String from, to;
    private Currency activeCurrency;
    private int fromId, toId;
    private double amount;

    public SettleUpTransaction(String from, int fromId, String to, int toId, double amount, Currency activeCurrency) {
        this.from = from;
        this.fromId = fromId;
        this.to = to;
        this.toId = toId;
        this.amount = amount;
        this.activeCurrency = activeCurrency;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Currency getCurrency() {
        return activeCurrency;
    }

    public int getFromId() {
        return fromId;
    }

    public int getToId() {
        return toId;
    }

    public double getAmount() {
        return amount;
    }
}
