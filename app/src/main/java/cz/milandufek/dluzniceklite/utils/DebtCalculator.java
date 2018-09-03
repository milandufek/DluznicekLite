package cz.milandufek.dluzniceklite.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
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

        List<HashMap<String, Object>> debts = new LinkedList<>();
        double tolerance = 0.001;

        int resolvedMembers = 0;
        int totalMembers = balances.size();
        //sort(balances, Balance.SortByBalance);

        for (Balance balance : balances) {
            Log.d(TAG, "Member: " +  balance.getMemberName());
            Log.d(TAG, "Balance: " + String.valueOf(balance.getBalance()));
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
                balance.setBalance(cursor.getDouble(2));
            balances.add(balance);
        }

        return balances;
    }
}
