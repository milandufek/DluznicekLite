package cz.milandufek.dluzniceklite.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.MemberBalance;
import cz.milandufek.dluzniceklite.models.SummaryTransactionItem;
import cz.milandufek.dluzniceklite.models.Transaction;
import cz.milandufek.dluzniceklite.utils.MyDbHelper;
import cz.milandufek.dluzniceklite.utils.MyNumbers;

public class TransactionSql implements BaseColumns {
    private static final String TAG = "TransactionSql";

    private Context context;

    public static final String TABLE_NAME = "transactions";
    static final String _DEBTOR_ID = "debtor_id";
    static final String _EXPENSE_ID = "expense_id";
    static final String _AMOUNT = "amount";
    static final String _SETTLEUP_TRANSACTION = "settleup_transaction";

    private static final String ALL_COLS[] = { _ID, _DEBTOR_ID, _EXPENSE_ID, _AMOUNT, _SETTLEUP_TRANSACTION};

    public static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE " +
            TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            _DEBTOR_ID + " INTEGER NOT NULL, " +
            _EXPENSE_ID + " INTEGER NOT NULL, " +
            _AMOUNT + " FLOAT NOT NULL, " +
            _SETTLEUP_TRANSACTION + " INTEGER DEFAULT NULL, " +
                "FOREIGN KEY (" + _DEBTOR_ID + " ) " +
                "REFERENCES " + GroupMemberSql.TABLE_NAME + "(" + GroupMemberSql._ID + ") " +
                "ON DELETE CASCADE, " +
                "FOREIGN KEY (" + _EXPENSE_ID + " ) " +
                "REFERENCES " + ExpenseSql.TABLE_NAME + "(" + ExpenseSql._ID + ") " +
                "ON DELETE CASCADE" +
            ");";

    private Transaction buildTransaction(Cursor cursor) {
        return new Transaction(
                cursor.getInt(cursor.getColumnIndexOrThrow(_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(_DEBTOR_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(_EXPENSE_ID)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(_AMOUNT)),
                MyNumbers.numberToBoolean(cursor.getInt(cursor.getColumnIndex(_SETTLEUP_TRANSACTION)))
        );
    }

    public long insertTransaction(Transaction transaction) {
        ContentValues values = new ContentValues();
        // _ID
        values.put(_AMOUNT, transaction.getAmount());
        values.put(_DEBTOR_ID, transaction.getDebtor_id());
        values.put(_EXPENSE_ID, transaction.getExpense_id());
        values.put(_SETTLEUP_TRANSACTION, transaction.getIsSettleUpTransaction());

        return MyDbHelper.getInstance(context)
                .getWritableDatabase()
                .insert(TABLE_NAME,null, values);
    }

    public long insertTransactions(List<Transaction> transactions) {
        SQLiteDatabase db = MyDbHelper.getInstance(context).getWritableDatabase();
        int rowsAffected = 0;
        try {
            db.beginTransaction();
            for (Transaction t : transactions) {
                ContentValues values = new ContentValues();
                values.put(_DEBTOR_ID, t.getDebtor_id());
                values.put(_EXPENSE_ID, t.getExpense_id());
                values.put(_AMOUNT, t.getAmount());
                values.put(_SETTLEUP_TRANSACTION, t.getIsSettleUpTransaction());

                if(db.insert(TABLE_NAME,null, values) > -1) {
                    rowsAffected++;
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }

        if (transactions.size() == rowsAffected) {
            return rowsAffected;
        } else {
            return -1;
        }
    }

    public List<Transaction> getTransactions(int expenseId) {
        String selection = _EXPENSE_ID + " = ?" + " AND " + _AMOUNT + " >= ?";
        String[] selectionArgs = { String.valueOf(expenseId), "0" };

        Cursor cursor = MyDbHelper.getInstance(context)
                .getReadableDatabase()
                .query(TABLE_NAME, ALL_COLS, selection, selectionArgs,
                null, null, _EXPENSE_ID);

        List<Transaction> transactions = new ArrayList<>();
        while (cursor.moveToNext()) {
            transactions.add(buildTransaction(cursor));
        }

        return transactions;
    }

    public int deleteTransaction(int id) {
        String selection = _ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        return MyDbHelper.getInstance(context)
                .getWritableDatabase()
                .delete(TABLE_NAME, selection, selectionArgs);
    }

    public int deleteTransactions(int expenseId) {
        String selection = _EXPENSE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(expenseId) };

        return MyDbHelper.getInstance(context)
                .getWritableDatabase()
                .delete(TABLE_NAME, selection, selectionArgs);
    }

    public List<SummaryTransactionItem> getTransactionsForExpense(int expenseId) {
        SQLiteDatabase db = MyDbHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT "
                + GroupMemberSql.TABLE_NAME + "." + GroupMemberSql._NAME + ", " + _AMOUNT +
                " FROM " + TABLE_NAME +
                " INNER JOIN " + GroupMemberSql.TABLE_NAME +
                " ON " + _DEBTOR_ID + " = " + GroupMemberSql.TABLE_NAME + "." + GroupMemberSql._ID +
                " WHERE " + _EXPENSE_ID + " = " + expenseId +
                " AND " + _AMOUNT + " >= " + 0 +
                ";";

        Cursor cursor = db.rawQuery(query, null, null);

        List<SummaryTransactionItem> transactions = new ArrayList<>();
        while (cursor.moveToNext()) {
            SummaryTransactionItem item = new SummaryTransactionItem(
                cursor.getString(cursor.getColumnIndexOrThrow(GroupMemberSql._NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(_AMOUNT))
            );
            transactions.add(item);
        }
        if (transactions.size() > 0) { cursor.close(); }

        return transactions;
    }

    /**
     * Select all transaction for DebtCalculator activity
     * @return cursor with transactions
     *
     *         SELECT debtor_id, group_members.name , SUM(amount * exchange_rate / quantity) AS balance
     *         FROM transactions
     *         INNER JOIN group_members
     *         ON transactions.debtor_id = group_members._id
     *         INNER JOIN expenses
     *         ON expenses._id = transactions.expense_id
     *         INNER JOIN currencies
     *         ON currencies._id = expenses.currency_id
     *         WHERE expenses.group_id = 1
     *         GROUP BY debtor_id
     *         ORDER BY debtor_id
     */
    public List<MemberBalance> getBalances(int groupId) {
        SQLiteDatabase db = MyDbHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT " +
                TransactionSql._DEBTOR_ID + ", " +
                GroupMemberSql.TABLE_NAME + "." + GroupMemberSql._NAME + ", " +
                "SUM(" +
                    TransactionSql._AMOUNT +
                    " * " +
                    CurrencySql._EXCHANGE_RATE +
                    " / " +
                    CurrencySql._QUANTITY +
                ")" + " AS balance " +
                " FROM " + TransactionSql.TABLE_NAME +
                " INNER JOIN " + GroupMemberSql.TABLE_NAME +
                " ON " +
                GroupMemberSql.TABLE_NAME + "." + GroupMemberSql._ID +
                " = " +
                TransactionSql.TABLE_NAME + "." + TransactionSql._DEBTOR_ID +
                " INNER JOIN " + ExpenseSql.TABLE_NAME +
                " ON " +
                ExpenseSql.TABLE_NAME + "." + ExpenseSql._ID +
                " = " +
                TransactionSql.TABLE_NAME + "." + TransactionSql._EXPENSE_ID +
                " INNER JOIN " + CurrencySql.TABLE_NAME +
                " ON " +
                CurrencySql.TABLE_NAME + "." + CurrencySql._ID +
                " = " +
                ExpenseSql.TABLE_NAME + "." + ExpenseSql._CURRENCY_ID +
                " WHERE " +
                ExpenseSql.TABLE_NAME + "." + ExpenseSql._GROUP_ID +
                " = " +
                groupId +
                " GROUP BY " + TransactionSql._DEBTOR_ID +
                " ORDER BY " + TransactionSql._DEBTOR_ID +
                ";";

        Cursor cursor = db.rawQuery(query, null, null);
        List<MemberBalance> memberBalances = new ArrayList<>();

        while (cursor.moveToNext()) {
            memberBalances.add(new MemberBalance(
                    groupId,
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2)
            ));
        }
        if (memberBalances.size() > 0) { cursor.close(); }

        return memberBalances;
    }

    public List<Integer> getActivePayersAndDebtorIds(int groupId) {
        SQLiteDatabase db = MyDbHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT DISTINCT " + _DEBTOR_ID +
                " FROM " + TABLE_NAME +
                " INNER JOIN " + ExpenseSql.TABLE_NAME +
                " ON " +
                ExpenseSql.TABLE_NAME + "." + ExpenseSql._ID +
                " = " +
                TransactionSql.TABLE_NAME + "." + TransactionSql._EXPENSE_ID +
                " WHERE " +
                ExpenseSql.TABLE_NAME + "." + ExpenseSql._GROUP_ID +
                " = " + groupId +
                ";";

        Cursor cursor = db.rawQuery(query, null, null);

        List<Integer> activeMemberIds = new ArrayList<>();

        while (cursor.moveToNext()) {
            activeMemberIds.add(cursor.getInt(cursor.getColumnIndexOrThrow(_DEBTOR_ID)));
        }

        if (activeMemberIds.size() > 0) { cursor.close(); }

        return activeMemberIds;
    }
}
