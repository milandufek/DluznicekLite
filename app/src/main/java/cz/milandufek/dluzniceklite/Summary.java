package cz.milandufek.dluzniceklite;

import android.content.Context;

import java.util.List;

import cz.milandufek.dluzniceklite.models.MemberBalance;
import cz.milandufek.dluzniceklite.models.SummaryExpenseItem;
import cz.milandufek.dluzniceklite.models.SummarySettleUpItem;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;
import cz.milandufek.dluzniceklite.utils.DebtCalculator;
import cz.milandufek.dluzniceklite.utils.MyPreferences;

class Summary {

    private CurrencyRepo sqlCurrency = new CurrencyRepo();

    SummaryExpenseItem initSummaryExpense(Context context) {
        MyPreferences sp = new MyPreferences(context);
        int groupId = sp.getActiveGroupId();
        int currencyId = sp.getActiveGroupCurrencyId();

        return new ExpenseRepo().selectTotalSpent(groupId, currencyId, false);
    }

    SummarySettleUpItem initSummarySettleUp(Context context) {
        MyPreferences sp = new MyPreferences(context);
        int currencyId = sp.getActiveGroupCurrencyId();

        List<MemberBalance> memberBalances = new TransactionRepo().getBalances(sp.getActiveGroupId());

        double sumAmountInBaseCurrency = 0;
        for(MemberBalance memberBalance : memberBalances) {
            if (memberBalance.getBalance() >= 0)
                sumAmountInBaseCurrency += memberBalance.getBalance();
        }

        int baseCurrency = sqlCurrency.getBaseCurrency().getId();
        String currencyName = sqlCurrency.getCurrency(currencyId).getName();
        double sumAmount = CurrencyOperations.exchangeAmount(sumAmountInBaseCurrency, baseCurrency, currencyId);
        if (sumAmount <= DebtCalculator.TOLERANCE)
            sumAmount = 0;

        return new SummarySettleUpItem(currencyId, currencyName, sumAmount);
    }
}
