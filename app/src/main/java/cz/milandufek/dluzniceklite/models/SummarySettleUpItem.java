package cz.milandufek.dluzniceklite.models;

import java.util.Observable;
import java.util.Observer;

public final class SummarySettleUpItem implements Observer {

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

    public String getCurrencyName() {
        return currencyName;
    }

    public double getSumToSettleUp() {
        return sumToSettleUp;
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO
    }

    @Override
    public String toString() {
        return SummarySettleUpItem.class.toString() + " = { " +
                "currencyId = " + currencyId +
                ", currencyName = " + currencyName +
                ", sumToSettleUp = " + sumToSettleUp +
                " }";
    }
}
