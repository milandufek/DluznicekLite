package cz.milandufek.dluzniceklite.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.Balance;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;

import static java.util.Collections.sort;

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
        Log.d(TAG, "calculateDebts method started");
        List<Balance> balances = new LinkedList<>(getBalances(groupId, currencyId));

        List<HashMap<String,Object>> debts = new LinkedList<>();
        double tolerance = 0.01;

        int resolvedMembers = 0;
        int totalMembers = balances.size();

        while (resolvedMembers != totalMembers) {
            sort(balances, Balance.SortByBalance);
            Balance creditor = balances.get(0);
            Balance debtor = balances.get(balances.size() - 1);

            double amount;
            double creditorShouldReceive = Math.abs(creditor.getBalance());
            double debtorShouldSend = Math.abs(debtor.getBalance());
            if (debtorShouldSend > creditorShouldReceive) {
                amount = creditorShouldReceive;
            } else {
                amount = debtorShouldSend;
            }

            creditor.setBalance(creditor.getBalance() + amount);
            debtor.setBalance(debtor.getBalance() - amount);

            HashMap<String,Object> values = new HashMap<>();
            values.put("from", debtor);
            values.put("amount", amount);
            values.put("to", creditor);
            debts.add(values);

            creditorShouldReceive = Math.abs(creditor.getBalance());
            debtorShouldSend = Math.abs(debtor.getBalance());
            if (creditorShouldReceive <= tolerance)
                resolvedMembers++;
            if (debtorShouldSend <= tolerance)
                resolvedMembers++;

            Iterator <HashMap<String,Object>> iterator = debts.iterator();
            while (iterator.hasNext()) {
                HashMap <String,Object> debt = iterator.next();
                if ((Double) debt.get("amount") <= tolerance)
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

        Cursor cursor = new TransactionRepo().selectBalances(groupId);
        while (cursor.moveToNext()) {
            Balance balance = new Balance();
                balance.setGroupId(groupId);
                balance.setMemberId(cursor.getInt(0));
                balance.setMemberName(cursor.getString(1));
                balance.setBalance(cursor.getDouble(2));
            balances.add(balance);
        }

        return balances;
    }
}
