package cz.milandufek.dluzniceklite.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import cz.milandufek.dluzniceklite.models.Balance;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;

import static java.util.Collections.sort;

/**
 * Class to settle up the debts
 * @author Milan Dufek
 */
public class DebtCalculator {

    private static final String TAG = DebtCalculator.class.toString();
    private static final double TOLERANCE = 0.1;

    public DebtCalculator() {
    }

    public List<HashMap<String,Object>> calculateDebts(int groupId, int currencyId) {
        Log.d(TAG, "calculateDebts method started");

        List<Balance> balances = new ArrayList<>(getBalances(groupId, currencyId));
        List<HashMap<String,Object>> debts = new ArrayList<>();

        int resolvedMembers = 0;
        int totalMembers = balances.size();

        while (resolvedMembers != totalMembers) {
            Log.d(TAG, "calculateDebts: Total members: " + totalMembers);
            Log.d(TAG, "calculateDebts: Resolved members " + resolvedMembers);

            sort(balances, Balance.SortByBalance);
            Log.d(TAG, "calculateDebts: balances " + balances);
            Balance creditor = balances.get(0);
            Balance debtor = balances.get(balances.size() - 1);

            // TODO tuning
            double amount;
            double creditorShouldReceive = Math.abs(creditor.getBalance());
            double debtorShouldSend = Math.abs(debtor.getBalance());
            if ((creditorShouldReceive + debtorShouldSend) <= (2 * TOLERANCE))
                break;
            if (debtorShouldSend > creditorShouldReceive) {
                amount = creditorShouldReceive;
            } else {
                amount = debtorShouldSend;
            }

            HashMap<String,Object> values = new HashMap<>();
            values.put("from", debtor);
            values.put("amount", amount);
            values.put("to", creditor);
            debts.add(values);

            creditor.setBalance(creditor.getBalance() + amount);
            debtor.setBalance(debtor.getBalance() - amount);

            creditorShouldReceive = Math.abs(creditor.getBalance());
            debtorShouldSend = Math.abs(debtor.getBalance());
            if (creditorShouldReceive <= TOLERANCE)
                resolvedMembers++;
            if (debtorShouldSend <= TOLERANCE)
                resolvedMembers++;

            ListIterator<HashMap<String,Object>> iterator = debts.listIterator();
            while (iterator.hasNext()) {
                double nextAmount = (double) iterator.next().get("amount");
                Log.d(TAG, "calculateDebts: amount " + nextAmount);
                if (nextAmount <= TOLERANCE) {
                    iterator.remove();
                }
            }
        }

        Log.d(TAG, "calculateDebts: debts " + debts.toString());

        return debts;
    }

    /**
     * Select balances for all members in group in base currency
     * @param groupId
     * @param currencyId
     * @return Cursor
     */
    public List<Balance> getBalances(int groupId, int currencyId) {
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
