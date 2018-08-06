package cz.milandufek.dluzniceklite.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.List;

import cz.milandufek.dluzniceklite.models.Transaction;
import cz.milandufek.dluzniceklite.utils.DbHelper;

public class TransactionRepo implements BaseColumns {
    private static final String TAG = "TransactionRepo";

    private Context context;

    public static final String TABLE_NAME = "transactions";
    static final String _DEBTOR_ID = "debtor_id";
    static final String _EXPENSE_ID = "expense_id";
    static final String _AMOUNT = "amount";

    private static final String ALL_COLS[] = { _ID, _DEBTOR_ID, _EXPENSE_ID, _AMOUNT};

    public static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE " +
            TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            _DEBTOR_ID + " INTEGER NOT NULL, " +
            _EXPENSE_ID + " INTEGER NOT NULL, " +
            _AMOUNT + " FLOAT NOT NULL, " +
                "FOREIGN KEY (" + _DEBTOR_ID + " ) " +
                "REFERENCES " + GroupMemberRepo.TABLE_NAME + "(" + GroupMemberRepo._ID + ") " +
                "ON DELETE CASCADE, " +
                "FOREIGN KEY (" + _EXPENSE_ID + " ) " +
                "REFERENCES " + ExpenseRepo.TABLE_NAME + "(" + ExpenseRepo._ID + ") " +
                "ON DELETE CASCADE" +
            ");";

    /**
     * Insert transaction into database
     * @param transaction
     * @return row id
     */
    public long insertTransaction(Transaction transaction) {
        ContentValues values = new ContentValues();
        // _ID
        values.put(_AMOUNT, transaction.getAmount());
        values.put(_DEBTOR_ID, transaction.getDebtor_id());
        values.put(_EXPENSE_ID, transaction.getExpense_id());

        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        return db.insert(TABLE_NAME,null, values);
    }

    /**
     * Insert multiple transaction into database
     * @param transactions
     * @return number of rows affected
     */
    public long insertTransactions(List<Transaction> transactions) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        int rowsAffected = 0;
        int transactionsCount = transactions.size();
        try {
            db.beginTransaction();
            for (int i = 0; transactionsCount > i; i++) {
                ContentValues values = new ContentValues();
                values.put(_DEBTOR_ID, transactions.get(i).getDebtor_id());
                values.put(_EXPENSE_ID, transactions.get(i).getExpense_id());
                values.put(_AMOUNT, transactions.get(i).getAmount());

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

        if (transactionsCount == rowsAffected) {
            return rowsAffected;
        } else {
            return -1;
        }
    }

    /**
     * Retrieve data
     * @param expenseId
     * @return transaction of expenseId
     */
    public Cursor selectTransactions(int expenseId) {
        String selection = _EXPENSE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(expenseId) };

        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        return db.query(TABLE_NAME, ALL_COLS, selection, selectionArgs,
                null, null, _EXPENSE_ID);
    }

    /**
     * Delete transaction from database
     * @param id
     * @return rows affected
     */
    public Integer deleteTransaction(int id) {
        String selection = _ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        return db.delete(TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Delete transactions from database
     * @param expenseId
     * @return rows affected
     */
    public Integer deleteTransactions(int expenseId) {
        String selection = _EXPENSE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(expenseId) };
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        return db.delete(TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Select transaction for expenses ID with positive amount
     * @param expenseId
     * @return transaction
     */
    public Cursor selectTransactionForExpense(int expenseId) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT "
                + GroupMemberRepo.TABLE_NAME + "." + GroupMemberRepo._NAME + ", " + _AMOUNT +
                " FROM " + TABLE_NAME +
                " INNER JOIN " + GroupMemberRepo.TABLE_NAME +
                " ON " + _DEBTOR_ID + " = " + GroupMemberRepo.TABLE_NAME + "." + GroupMemberRepo._ID +
                " WHERE " + _EXPENSE_ID + " = " + expenseId +
                " AND " + _AMOUNT + " >= " + 0 +
                ";";

        return db.rawQuery(query, null, null);
    }

    /**
     * Select all transaction for DebtCalculator activity
     * @param groupId
     * @return cursor with transactions
     */
    public Cursor selectBalance(int groupId) {
//        SELECT debtor_id, group_members.name , SUM(amount) AS balance, currencies._id, currencies.quantity, currencies.exchange_rate
//        FROM transactions
//        INNER JOIN group_members
//        ON transactions.debtor_id = group_members._id
//        INNER JOIN expenses
//        ON expenses._id = transactions.expense_id
//        INNER JOIN currencies
//        ON currencies._id = expenses.currency_id
//        WHERE expenses.group_id = 1
//        GROUP BY debtor_id, currencies._id
//        ORDER BY debtor_id
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT " +
                TransactionRepo._DEBTOR_ID + ", " +
                GroupMemberRepo.TABLE_NAME + "." + GroupMemberRepo._NAME + ", " +
                "SUM(" + TransactionRepo.TABLE_NAME + "." + TransactionRepo._AMOUNT + ")" + " AS balance " + ", " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._ID + " AS currency_id " + ", " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._QUANTITY + ", " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._EXCHANGE_RATE +
                " FROM " + TransactionRepo.TABLE_NAME +
                " INNER JOIN " + GroupMemberRepo.TABLE_NAME +
                " ON " +
                GroupMemberRepo.TABLE_NAME + "." + GroupMemberRepo._ID +
                " = " +
                TransactionRepo.TABLE_NAME + "." + TransactionRepo._DEBTOR_ID +
                " INNER JOIN " + ExpenseRepo.TABLE_NAME +
                " ON " +
                ExpenseRepo.TABLE_NAME + "." + ExpenseRepo._ID +
                " = " +
                TransactionRepo.TABLE_NAME + "." + TransactionRepo._EXPENSE_ID +
                " INNER JOIN " + CurrencyRepo.TABLE_NAME +
                " ON " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._ID +
                " = " +
                TransactionRepo.TABLE_NAME + "." + TransactionRepo._ID +
        " WHERE " +
                ExpenseRepo.TABLE_NAME + "." + ExpenseRepo._GROUP_ID +
                " = " +
                groupId +
                " GROUP BY " + TransactionRepo._DEBTOR_ID + ", currency_id" +
                " ORDER BY " + TransactionRepo._DEBTOR_ID +
                ";";

        return db.rawQuery(query, null, null);
    }
}
