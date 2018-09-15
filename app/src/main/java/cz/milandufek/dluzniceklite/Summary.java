package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.database.Cursor;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.models.SummaryExpense;
import cz.milandufek.dluzniceklite.models.SummaryItem;
import cz.milandufek.dluzniceklite.models.SummarySettleUp;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;
import cz.milandufek.dluzniceklite.utils.MyNumbers;
import cz.milandufek.dluzniceklite.utils.MyPreferences;

public class Summary {

    private int type;

    // TODO resolve both summary types in one method

    SummaryExpense initExpenseSummary(Context context) {
        MyPreferences sp = new MyPreferences(context);
        int groupId = sp.getActiveGroupId();

        CurrencyRepo sqlCurrency = new CurrencyRepo();

        Cursor cursorSummary = new ExpenseRepo().selectTotalSpent(groupId, false);
        double sumAmountInBaseCurrency = 0;
        while (cursorSummary.moveToNext()) {
            int quantity = cursorSummary.getInt(2);
            int exchangeRate = cursorSummary.getInt(3);
            double amount = cursorSummary.getDouble(4);
            sumAmountInBaseCurrency += ( amount / quantity * exchangeRate );
        }

        double sumAmount = sumAmountInBaseCurrency;
        String currencyName;
        int currencyId = sp.getActiveGroupCurrency();
        int baseCurrency = sqlCurrency.getBaseCurrency();
        if (currencyId != baseCurrency) {
            Currency activeCurrency = sqlCurrency.getCurrency(currencyId);
            currencyId = activeCurrency.getId();
            sumAmount = ( sumAmountInBaseCurrency / activeCurrency.getExchangeRate() * activeCurrency.getQuantity() );
            currencyName = activeCurrency.getName();
        } else {
            currencyName = sqlCurrency.getCurrency(1).getName();
        }

        sumAmount *= -1;
        sumAmount = MyNumbers.roundIt(sumAmount, 2);

        return new SummaryExpense(currencyId, currencyName, sumAmount);
    }

    SummarySettleUp initSettleUpSummary(Context context) {
        MyPreferences sp = new MyPreferences(context);
        int groupId = sp.getActiveGroupId();

        CurrencyRepo sqlCurrency = new CurrencyRepo();

        Cursor cursorSummary = new ExpenseRepo().selectTotalSpent(groupId, true);
        double sumAmountInBaseCurrency = 0;
        while (cursorSummary.moveToNext()) {
            int quantity = cursorSummary.getInt(2);
            int exchangeRate = cursorSummary.getInt(3);
            double amount = cursorSummary.getDouble(4);
            sumAmountInBaseCurrency += ( amount / quantity * exchangeRate );
        }

        double sumAmount = sumAmountInBaseCurrency;
        String currencyName;
        int currencyId = sp.getActiveGroupCurrency();
        int baseCurrency = sqlCurrency.getBaseCurrency();
        if (currencyId != baseCurrency) {
            Currency activeCurrency = sqlCurrency.getCurrency(currencyId);
            currencyId = activeCurrency.getId();
            sumAmount = ( sumAmountInBaseCurrency / activeCurrency.getExchangeRate() * activeCurrency.getQuantity() );
            currencyName = activeCurrency.getName();
        } else {
            currencyName = sqlCurrency.getCurrency(1).getName();
        }

        sumAmount *= -1;
        sumAmount = MyNumbers.roundIt(sumAmount, 2);

        return new SummarySettleUp(currencyId, currencyName, sumAmount);
    }
}
