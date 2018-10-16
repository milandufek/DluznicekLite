package cz.milandufek.dluzniceklite;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.utils.MyNumbers;

public class CurrencyOperations {

    private static final String TAG = CurrencyOperations.class.toString();

    private CurrencyOperations() {
    }

    public static double exchangeAmount(double amount, int originCurrencyId, int newCurrencyId) {

        CurrencyRepo sql = new CurrencyRepo();
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
