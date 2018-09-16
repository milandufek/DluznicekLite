package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.database.Cursor;

import java.util.List;

import cz.milandufek.dluzniceklite.models.Balance;
import cz.milandufek.dluzniceklite.models.SummaryExpense;
import cz.milandufek.dluzniceklite.models.SummarySettleUp;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;
import cz.milandufek.dluzniceklite.utils.DebtCalculator;
import cz.milandufek.dluzniceklite.utils.MyPreferences;

public class Summary {

    private CurrencyRepo sqlCurrency = new CurrencyRepo();

    SummaryExpense initSummaryExpense(Context context) {
        MyPreferences sp = new MyPreferences(context);
        int groupId = sp.getActiveGroupId();

        Cursor cursorSummary = new ExpenseRepo().selectTotalSpent(groupId, false);
        double sumAmountInBaseCurrency = 0;
        while (cursorSummary.moveToNext()) {
            int quantity = cursorSummary.getInt(2);
            int exchangeRate = cursorSummary.getInt(3);
            double amount = cursorSummary.getDouble(4);
            sumAmountInBaseCurrency += ( amount / quantity * exchangeRate ) * -1;
        }

        int currencyId = sp.getActiveGroupCurrencyId();
        String currencyName = sqlCurrency.getCurrency(currencyId).getName();
        int baseCurrency = sqlCurrency.getBaseCurrency().getId();
        double sumAmount = CurrencyOperation.exchangeAmount(sumAmountInBaseCurrency, baseCurrency, currencyId);

        return new SummaryExpense(currencyId, currencyName, sumAmount);
    }

    SummarySettleUp initSummarySettleUp(Context context) {
        MyPreferences sp = new MyPreferences(context);
        int groupId = sp.getActiveGroupId();
        int currencyId = sp.getActiveGroupCurrencyId();

        List<Balance> balances = new DebtCalculator().getBalances(groupId, currencyId);

        double sumAmountInBaseCurrency = 0;
        for(Balance balance : balances) {
            if (balance.getBalance() >= 0)
                sumAmountInBaseCurrency += balance.getBalance();
        }

        int baseCurrency = sqlCurrency.getBaseCurrency().getId();
        String currencyName = sqlCurrency.getCurrency(currencyId).getName();
        double sumAmount = CurrencyOperation.exchangeAmount(sumAmountInBaseCurrency, baseCurrency, currencyId);

        return new SummarySettleUp(currencyId, currencyName, sumAmount);
    }
}
