package cz.milandufek.dluzniceklite.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.CurrencyOperation;
import cz.milandufek.dluzniceklite.models.Expense;
import cz.milandufek.dluzniceklite.models.ExpenseItem;
import cz.milandufek.dluzniceklite.models.SummaryExpenseItem;
import cz.milandufek.dluzniceklite.models.SummaryTransactionItem;
import cz.milandufek.dluzniceklite.utils.DebtCalculator;
import cz.milandufek.dluzniceklite.utils.MyDbHelper;
import cz.milandufek.dluzniceklite.utils.MyNumbers;

public class ExpenseRepo implements BaseColumns {

    private Context context;

    public static final String TABLE_NAME = "expenses";
    private static final String _PAYER_ID = "payer_id";
    static final String _GROUP_ID = "group_id";
    static final String _CURRENCY_ID = "currency_id";
    private static final String _REASON = "reason";
    private static final String _DATE = "date";
    private static final String _TIME = "time";

    private static final String ALL_COLS[] = { _ID, _PAYER_ID, _GROUP_ID, _CURRENCY_ID,
            _REASON, _DATE, _TIME};

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

        SQLiteDatabase db = MyDbHelper.getInstance(context).getWritableDatabase();

        return db.insert(TABLE_NAME,null, values);
    }

    /**
     * Retrieve data
     * @param groupId
     * @return expenses of group
     */
    public List<Expense> selectExpenses(int groupId) {
        String selection = _GROUP_ID + " = ?";
        String[] selectionArgs = { String.valueOf(groupId) };

        SQLiteDatabase db = MyDbHelper.getInstance(context).getReadableDatabase();
        List<Expense> expenses = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME, ALL_COLS, selection, selectionArgs,
                null, null, _GROUP_ID);

        while (cursor.moveToNext()) {
            expenses.add(new Expense(
                    cursor.getInt(cursor.getColumnIndexOrThrow(_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(_PAYER_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(_GROUP_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(_CURRENCY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(_REASON)),
                    cursor.getString(cursor.getColumnIndexOrThrow(_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(_TIME)))
            );
        }

        return expenses;
    }

    /**
     * Delete expense from database
     * @param id
     * @return
     */
    public int deleteExpense(int id) {
        String selection = _ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        SQLiteDatabase db = MyDbHelper.getInstance(context).getWritableDatabase();

        return db.delete(TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Select all expenses for group in format suitable for ExpenseItem
     * @param groupId
     */
    public List<ExpenseItem> getAllExpenseItems(int groupId) {
        String query = "SELECT " +
                TABLE_NAME + "." + _ID + ", " +
                GroupMemberRepo.TABLE_NAME + "." + GroupMemberRepo._NAME + " AS " + GroupMemberRepo._NAME + ", " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._NAME + " AS " + CurrencyRepo._NAME + ", " +
                _REASON + ", " + _DATE + ", " + _TIME +
                " FROM " + TABLE_NAME +
                " INNER JOIN " + GroupMemberRepo.TABLE_NAME +
                " ON " + _PAYER_ID + " = " + GroupMemberRepo.TABLE_NAME + "." + GroupMemberRepo._ID +
                " INNER JOIN " + CurrencyRepo.TABLE_NAME +
                " ON " + TABLE_NAME + "." + _CURRENCY_ID + " = " + CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._ID +
                " WHERE " + TABLE_NAME + "." + _GROUP_ID + " = " + groupId +
                " ORDER BY " + _DATE + " DESC, " + _TIME + " DESC" +
                ";";
        SQLiteDatabase db = MyDbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null, null);

        List<ExpenseItem> expenseItems = new ArrayList<>();
        while (cursor.moveToNext()) {
            int expenseId = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
            double sum = 0;
            List<SummaryTransactionItem> transactions = new TransactionRepo()
                    .getTransactionsForExpense(expenseId);
            StringBuilder debtors = new StringBuilder();
            String separator = "";
            for (SummaryTransactionItem item : transactions) {
                debtors.append(separator);
                separator = ", ";
                debtors.append(item.getMemberName()); // member name
                sum += item.getAmount(); // amount
            }
            sum = MyNumbers.roundIt(sum, 2);

            ExpenseItem expenseItem = new ExpenseItem(
                    expenseId,
                    cursor.getString(cursor.getColumnIndexOrThrow(_REASON)),
                    cursor.getString(cursor.getColumnIndexOrThrow(GroupMemberRepo._NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CurrencyRepo._NAME)),
                    debtors.toString(),
                    sum,
                    cursor.getString(cursor.getColumnIndexOrThrow(_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(_TIME))
            );

            expenseItems.add(expenseItem);
        }

        return expenseItems;
    }

    /**
     * Select summary
     */
    public SummaryExpenseItem selectTotalSpent(int groupId, int activeGroupCurrencyId, boolean includeSettleUps) {
//        SELECT currencies._id, currencies.name, currencies.quantity, currencies.exchange_rate, SUM(transactions.amount)
//        FROM expenses
//        INNER JOIN currencies ON expenses.currency_id = currencies._id
//        INNER JOIN transactions ON expenses._id = transactions.expense_id
//        WHERE expenses.group_id = 1 AND transactions.amount >= 0
//        GROUP BY currencies.name;
        String query = "SELECT " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._ID + ", " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._NAME + ", " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._QUANTITY + ", " +
                CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._EXCHANGE_RATE + ", " +
                "SUM(" + TransactionRepo.TABLE_NAME + "." + TransactionRepo._AMOUNT + ") AS " + TransactionRepo._AMOUNT +
                " FROM " + TABLE_NAME +
                " INNER JOIN " + CurrencyRepo.TABLE_NAME +
                " ON " + TABLE_NAME + "." + _CURRENCY_ID + " = " +
                    CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._ID +
                " INNER JOIN " + TransactionRepo.TABLE_NAME +
                " ON " + TABLE_NAME + "." + _ID + " = " +
                    TransactionRepo.TABLE_NAME + "." + TransactionRepo._EXPENSE_ID +
                " WHERE " + TABLE_NAME + "." + _GROUP_ID + " = " + groupId +
                    " AND " +
                    TransactionRepo.TABLE_NAME + "." + TransactionRepo._AMOUNT + " <= " + 0 +
                    " AND " +
                    TransactionRepo.TABLE_NAME + "." + TransactionRepo._SETTLEUP_TRANSACTION +
                        " <= " + MyNumbers.booleanToNumber(includeSettleUps) +
                " GROUP BY " + CurrencyRepo.TABLE_NAME + "." + CurrencyRepo._NAME +
                ";";

        SQLiteDatabase db = MyDbHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null, null);

        double sumAmountInBaseCurrency = 0;
        while (cursor.moveToNext()) {
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(CurrencyRepo._QUANTITY));
            int exchangeRate = cursor.getInt(cursor.getColumnIndexOrThrow(CurrencyRepo._EXCHANGE_RATE));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionRepo._AMOUNT));
            sumAmountInBaseCurrency += ( amount / quantity * exchangeRate ) * -1;
        }

        CurrencyRepo sqlCurrency = new CurrencyRepo();
        String currencyName = sqlCurrency.getCurrency(activeGroupCurrencyId).getName();
        int baseCurrency = sqlCurrency.getBaseCurrency().getId();
        double sumAmount = CurrencyOperation.exchangeAmount(sumAmountInBaseCurrency, baseCurrency, activeGroupCurrencyId);
        if (sumAmount <= DebtCalculator.TOLERANCE)
            sumAmount = 0;

        return new SummaryExpenseItem(activeGroupCurrencyId, currencyName, sumAmount);
    }

}
