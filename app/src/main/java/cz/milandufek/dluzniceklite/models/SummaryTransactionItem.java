package cz.milandufek.dluzniceklite.models;

public final class SummaryTransactionItem {

    private String memberName;
    private double amount;

    public SummaryTransactionItem(String memberName, double amount) {
        this.memberName = memberName;
        this.amount = amount;
    }

    public String getMemberName() {
        return memberName;
    }

    public double getAmount() {
        return amount;
    }
}
