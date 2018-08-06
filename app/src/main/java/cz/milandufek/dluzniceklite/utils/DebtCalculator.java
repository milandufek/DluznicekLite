package cz.milandufek.dluzniceklite.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.Balance;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;

/**
 * Class to settle up the debts
 * @author Milan Dufek
 */
public class DebtCalculator {
    private static final String TAG = "DebtCalculator";
    private Context context;

    public DebtCalculator() {
    }

    public List<HashMap<String, Object>> calculateDebts(int groupId, int currencyId) {
        List<Balance> balances = new LinkedList<>(getBalances(groupId, currencyId));

        List<HashMap<String, Object>> debts = new LinkedList<>();
        double tolerance = 0.001;

        int resolvedMembers = 0;
        int totalMembers = balances.size();
        while (resolvedMembers != totalMembers) {
            Collections.sort(balances);
            Balance debtor   = balances.get(0);
            Balance creditor = balances.get(balances.size() - 1);

            double debtorShouldSend = debtor.getBalance();
            double creditorShouldReceive = creditor.getBalance();
            double amount;
            if (debtorShouldSend > creditorShouldReceive) {
                amount = debtorShouldSend;
            } else {
                amount = creditorShouldReceive;
            }
            debtor.setBalance(debtor.getBalance() + amount);
            creditor.setBalance(creditor.getBalance() - amount);

            // set transaction for settle-up
            HashMap<String, Object> transaction = new HashMap<>();
            transaction.put("from", debtor);
            transaction.put("to", creditor);
            transaction.put("amount", amount);
            debts.add(transaction);

            // delete members with balance lower than tolerance
            debtorShouldSend = debtor.getBalance();
            creditorShouldReceive = creditor.getBalance();
            if (debtorShouldSend <= tolerance) {
                balances.remove(0);
                resolvedMembers++;
            }
            if (creditorShouldReceive <= tolerance) {
                balances.remove(balances.size() - 1);
                resolvedMembers++;
            }

            // remove transaction with less than tolerance
            Iterator<HashMap<String, Object>> iterator = debts.iterator();
            while (iterator.hasNext()) {
                HashMap<String, Object> debt = iterator.next();
                if ((double) debt.get("amount") <= tolerance)
                    iterator.remove();
            }
        }

        return debts;
    }

    /**
     * Select balances for all members in group in base currency
     * @param groupId
     * @param currencyId
     * @return Cursor
     */
    private List<Balance> getBalances(int groupId, int currencyId) {
        List<Balance> balances = new ArrayList<>();

        Cursor cursor = new TransactionRepo().selectBalance(groupId);
        while (cursor.moveToNext()) {
            Balance balance = new Balance();
                balance.setGroupId(groupId);
                balance.setMemberId(cursor.getInt(0));
                balance.setMemberName(cursor.getString(1));
                int expenseCurrencyId = cursor.getInt(3);
                if (expenseCurrencyId != currencyId) {
                    int quantity = cursor.getInt(4);
                    int exchangeRate = cursor.getInt(5);
                    double amount = cursor.getDouble(2);
                    balance.setBalance( amount / quantity * exchangeRate );
                }
            balances.add(balance);
        }

        return balances;
    }
}
