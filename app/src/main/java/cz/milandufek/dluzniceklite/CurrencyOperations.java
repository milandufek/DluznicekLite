package cz.milandufek.dluzniceklite;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.sql.CurrencySql;
import cz.milandufek.dluzniceklite.utils.MyNumbers;

public class CurrencyOperations {

    private static final String TAG = CurrencyOperations.class.toString();

    private CurrencyOperations() {
    }

    public static double exchangeAmount(double amount, int originCurrencyId, int newCurrencyId) {

        CurrencySql sql = new CurrencySql();
        double exchangedAmount;

        if (newCurrencyId == sql.getBaseCurrency().getId()) {
            exchangedAmount = amount;
        } else {
                Currency originCurrency = sql.getCurrency(originCurrencyId);
                Currency newCurrency = sql.getCurrency(newCurrencyId);
                exchangedAmount = ( amount * originCurrency.getExchangeRate() / originCurrency.getQuantity() );
                exchangedAmount = ( exchangedAmount / newCurrency.getExchangeRate() * newCurrency.getQuantity() );
        }

        return MyNumbers.roundIt(exchangedAmount, 2);
    }
}
