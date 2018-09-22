package cz.milandufek.dluzniceklite.models;

public final class SummarySettleUpItem {

    private int currencyId;
    private String currencyName;
    private double sumToSettleUp;

    public SummarySettleUpItem(int currencyId, String currencyName, double sumToSettleUp) {
        this.currencyId = currencyId;
        this.currencyName = currencyName;
        this.sumToSettleUp = sumToSettleUp;
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

    public double getSumToSettleUp() {
        return sumToSettleUp;
    }

    public void setSumToSettleUp(double sumToSettleUp) {
        this.sumToSettleUp = sumToSettleUp;
    }

}
