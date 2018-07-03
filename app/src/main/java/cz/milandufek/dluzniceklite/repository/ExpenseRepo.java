package cz.milandufek.dluzniceklite.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import cz.milandufek.dluzniceklite.models.Expense;
import cz.milandufek.dluzniceklite.utils.DbHelper;

public class ExpenseRepo implements BaseColumns {
    private static final String TAG = "ExpenseRepo";

    private Context context;

    public static final String TABLE_NAME = "expenses";
    private static final String _PAYER_ID = "payer_id";
    private static final String _GROUP_ID = "group_id";
    private static final String _CURRENCY_ID = "currency_id";
    private static final String _REASON = "reason";
    private static final String _DATE = "date";
    private static final String _TIME = "time";

    private static final String ALL_COLS[] = { _ID, _PAYER_ID, _GROUP_ID, _CURRENCY_ID,
            _REASON, _DATE, _TIME};

    // TODO date & time format, ORDER BY doesn't work properly
    public static final String CREATE_TABLE_EXPENSE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            _PAYER_ID + " TEXT NOT NULL, " +
            _GROUP_ID + " INTEGER NOT NULL, " +
            _CURRENCY_ID + " INTEGER NOT NULL, " +
            _REASON + " TEXT, " +
            _DATE + " TEXT, " +
            _TIME + " TEXT, " +
            "FOREIGN KEY ( " + _GROUP_ID + " ) " +
                "REFERENCES " + GroupRepo.TABLE_NAME + "(" + GroupRepo._ID + ") " +
                "ON DELETE CASCADE, " +
            "FOREIGN KEY ( " + _CURRENCY_ID + " ) " +
                "REFERENCES " + CurrencyRepo.TABLE_NAME + "( "+ CurrencyRepo._ID + " ) " +
                "ON DELETE CASCADE " +
            ");";

    /**
     * Insert expense into database
     * @param expense
     * @return row id
     */
    public long insertExpense(Expense expense) {
        ContentValues values = new ContentValues();
        // _ID
        values.put(_GROUP_ID, expense.getGroupId());
        values.put(_PAYER_ID, expense.getPayerId());
        values.put(_GROUP_ID, expense.getGroupId());
        values.put(_CURRENCY_ID, expense.getCurrencyId());
        values.put(_REASON, expense.getReason());
        values.put(_DATE, expense.getDate());
        values.put(_TIME, expense.getTime());

        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        return db.insert(TABLE_NAME,null, values);
    }

    /**
     * Retrieve data
     * @param groupId
     * @return expenses of group
     */
    public Cursor selectExpenses(int groupId) {
        String selection = _GROUP_ID + " = ?";
        String[] selectionArgs = { String.valueOf(groupId) };

        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        return db.query(TABLE_NAME, ALL_COLS, selection, selectionArgs,
                null, null, _GROUP_ID);

    }

    /**
     * Delete expense from database
     * @param id
     * @return
     */
    public Integer deleteExpense(int id) {
        String selection = _ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        return db.delete(TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Select all expenses in format suitable for ExpenseItem
     */
    public Cursor selectExpenseItems(int groupId) {
        String query = "SELECT " +
                TABLE_NAME + "." + _ID + ", " +
                GroupMemberRepo.TABLE_NAME + "." + GroupMemberRepo._NAME + ", " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._NAME + ", " +
                _REASON + ", " + _DATE + ", " + _TIME +
                " FROM " + TABLE_NAME +
                " INNER JOIN " + GroupMemberRepo.TABLE_NAME +
                " ON " + _PAYER_ID + " = " + GroupMemberRepo.TABLE_NAME + "." + GroupMemberRepo._ID +
                " INNER JOIN " + CurrencyRepo.TABLE_NAME +
                " ON " + TABLE_NAME + "." + _CURRENCY_ID + " = " + CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._ID +
                " WHERE " + TABLE_NAME + "." + _GROUP_ID + " = " + groupId +
                " ORDER BY " + _DATE + ", " + _TIME +
                " DESC;";
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        return db.rawQuery(query, null, null);
    }

    /**
     * Select summary
     */
    public Cursor selectTotalSpent(int groupId) {
//        SELECT currencies._id, currencies.name, currencies.quantity, currencies.exchange_rate, SUM(transactions.amount)
//        FROM expenses
//        INNER JOIN currencies ON expenses.currency_id = currencies._id
//        INNER JOIN transactions ON expenses._id = transactions.expense_id
//        WHERE expenses.group_id = 1
//        GROUP BY currencies.name;
        String query = "SELECT " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._ID + ", " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._NAME + ", " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._QUANTITY + ", " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._EXCHANGE_RATE + ", " +
                "SUM(" + TransactionRepo.TABLE_NAME + "." + TransactionRepo._AMOUNT + ") " +
                " FROM " + TABLE_NAME +
                " INNER JOIN " + CurrencyRepo.TABLE_NAME +
                " ON " + TABLE_NAME + "." + _CURRENCY_ID + " = " + CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._ID +
                " INNER JOIN " + TransactionRepo.TABLE_NAME +
                " ON " + TABLE_NAME + "." + _ID + " = " + TransactionRepo.TABLE_NAME + "." + TransactionRepo._EXPENSE_ID +
                " WHERE " + TABLE_NAME + "." + _GROUP_ID + " = " + groupId +
                " GROUP BY " + CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._NAME +
                ";";

        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        return db.rawQuery(query, null, null);
    }

}
