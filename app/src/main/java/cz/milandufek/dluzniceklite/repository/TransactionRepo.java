package cz.milandufek.dluzniceklite.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

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

        return  db.delete(TABLE_NAME, selection, selectionArgs);
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

    public Cursor selectTransactionForExpense(int expenseId) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT "
                + GroupMemberRepo.TABLE_NAME + "." + GroupMemberRepo._NAME + ", " + _AMOUNT +
                " FROM " + TABLE_NAME +
                " INNER JOIN " + GroupMemberRepo.TABLE_NAME +
                " ON " + _DEBTOR_ID + " = " + GroupMemberRepo.TABLE_NAME + "." + GroupMemberRepo._ID +
                " WHERE " + _EXPENSE_ID + " = " + expenseId +
                ";";

        return db.rawQuery(query, null, null);
    }
}
