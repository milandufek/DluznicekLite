package cz.milandufek.dluzniceklite.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.CurrencyOperations;
import cz.milandufek.dluzniceklite.models.Expense;
import cz.milandufek.dluzniceklite.models.ExpenseItem;
import cz.milandufek.dluzniceklite.models.SummaryExpenseItem;
import cz.milandufek.dluzniceklite.models.SummaryTransactionItem;
import cz.milandufek.dluzniceklite.utils.DebtCalculator;
import cz.milandufek.dluzniceklite.utils.MyDbHelper;
import cz.milandufek.dluzniceklite.utils.MyNumbers;

public class ExpenseSql implements BaseColumns {

    private static final String TAG = "ExpenseSql";

    private Context context;

    public static final String TABLE_NAME = "expenses";
    private static final String _PAYER_ID = "payer_id";
    static final String _GROUP_ID = "group_id";
    static final String _CURRENCY_ID = "currency_id";
    private static final String _REASON = "reason";
    private static final String _DATE = "date";
    private static final String _TIME = "time";

    private static final String ALL_COLS[] = {_ID, _PAYER_ID, _GROUP_ID, _CURRENCY_ID,
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
            "REFERENCES " + GroupSql.TABLE_NAME + "(" + GroupSql._ID + ") " +
            "ON DELETE CASCADE, " +
            "FOREIGN KEY ( " + _CURRENCY_ID + " ) " +
            "REFERENCES " + CurrencySql.TABLE_NAME + "( " + CurrencySql._ID + " ) " +
            "ON DELETE CASCADE " +
            ");";

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

        return MyDbHelper.getInstance(context)
                .getWritableDatabase()
                .insert(TABLE_NAME, null, values);
    }

    public List<Expense> selectExpenses(int groupId) {
        String selection = _GROUP_ID + " = ?";
        String[] selectionArgs = {String.valueOf(groupId)};

        List<Expense> expenses = new ArrayList<>();

        Cursor cursor = MyDbHelper.getInstance(context)
                .getReadableDatabase()
                .query(TABLE_NAME, ALL_COLS, selection, selectionArgs, null, null, _GROUP_ID);

        while (cursor.moveToNext()) {
            expenses.add(buildExpense(cursor));
        }

        return expenses;
    }

    public Expense getExpense(int expenseId) {
        String selection = _ID + " = ?";
        String[] selectionArgs = {String.valueOf(expenseId)};

        Cursor cursor = MyDbHelper.getInstance(context)
                .getReadableDatabase()
                .query(TABLE_NAME, ALL_COLS, selection, selectionArgs, null, null, null);

        Expense expense = null;
        while (cursor.moveToNext()) {
            expense = buildExpense(cursor);
            cursor.close();
        }

        return expense;
    }

    public int deleteExpense(int id) {
        String selection = _ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        return MyDbHelper.getInstance(context)
                .getWritableDatabase()
                .delete(TABLE_NAME, selection, selectionArgs);
    }

    public boolean updateExpense(Expense expense) {
        ContentValues values = new ContentValues();
        values.put(_PAYER_ID,expense.getPayerId());
        values.put(_GROUP_ID,expense.getGroupId());
        values.put(_CURRENCY_ID, expense.getCurrencyId());
        values.put(_REASON, expense.getReason());
        values.put(_DATE, expense.getDate());
        values.put(_TIME, expense.getTime());

        int result = MyDbHelper.getInstance(context).getWritableDatabase()
                .update(TABLE_NAME, values,
                        "_ID = ?",
                        new String[] { String.valueOf(expense.getId()) });

        return result == 1;
    }

    public List<ExpenseItem> getAllExpenseItems(int groupId) {
        String query = "SELECT " +
                TABLE_NAME + "." + _ID + ", " +
                _REASON + ", " +
                GroupMemberSql.TABLE_NAME + "." + GroupMemberSql._NAME + " AS PAYER" + ", " +
                CurrencySql.TABLE_NAME + "." + CurrencySql._NAME + " AS CURRENCY" + ", " +
                _DATE + ", " + _TIME +
                " FROM " + TABLE_NAME +
                " INNER JOIN " + GroupMemberSql.TABLE_NAME +
                " ON " + _PAYER_ID + " = " + GroupMemberSql.TABLE_NAME + "." + GroupMemberSql._ID +
                " INNER JOIN " + CurrencySql.TABLE_NAME +
                " ON " + TABLE_NAME + "." + _CURRENCY_ID + " = " + CurrencySql.TABLE_NAME + "." + CurrencySql._ID +
                " WHERE " + TABLE_NAME + "." + _GROUP_ID + " = " + groupId +
                " ORDER BY " + _DATE + " DESC, " + _TIME + " DESC" +
                ";";

        Cursor cursor = MyDbHelper.getInstance(context)
                .getReadableDatabase()
                .rawQuery(query, null, null);

        List<ExpenseItem> expenseItems = new ArrayList<>();
        while (cursor.moveToNext()) {
            int expenseId = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));

            List<SummaryTransactionItem> transactions = new TransactionSql()
                    .getTransactionsForExpense(expenseId);

            double sum = 0;
            StringBuilder debtors = new StringBuilder();
            String separator = "";
            for (SummaryTransactionItem item : transactions) {
                debtors.append(separator);
                separator = ", ";
                debtors.append(item.getMemberName());
                sum += item.getAmount();
            }

            ExpenseItem expenseItem = new ExpenseItem(
                    expenseId,
                    cursor.getString(cursor.getColumnIndexOrThrow(_REASON)),
                    cursor.getString(cursor.getColumnIndexOrThrow("PAYER")),
                    cursor.getString(cursor.getColumnIndexOrThrow("CURRENCY")),
                    debtors.toString(),
                    MyNumbers.roundIt(sum, 2),
                    cursor.getString(cursor.getColumnIndexOrThrow(_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(_TIME))
            );

            expenseItems.add(expenseItem);
        }

        if (expenseItems.size() > 0) { cursor.close(); }

        return expenseItems;
    }

    /**
     * Select summary
     *         SELECT currencies._id, currencies.name, currencies.quantity, currencies.exchange_rate, SUM(transactions.amount)
     *         FROM expenses
     *         INNER JOIN currencies ON expenses.currency_id = currencies._id
     *         INNER JOIN transactions ON expenses._id = transactions.expense_id
     *         WHERE expenses.group_id = 1 AND transactions.amount >= 0
     *         GROUP BY currencies.name;
     */
    public SummaryExpenseItem selectTotalSpent(int groupId, int activeGroupCurrencyId, boolean includeSettleUps) {

        String query = "SELECT " +
                CurrencySql.TABLE_NAME + "." + CurrencySql._ID + ", " +
                CurrencySql.TABLE_NAME + "." + CurrencySql._NAME + ", " +
                CurrencySql.TABLE_NAME + "." + CurrencySql._QUANTITY + ", " +
                CurrencySql.TABLE_NAME + "." + CurrencySql._EXCHANGE_RATE + ", " +
                "SUM(" + TransactionSql.TABLE_NAME + "." + TransactionSql._AMOUNT + ") AS " + TransactionSql._AMOUNT +
                " FROM " + TABLE_NAME +
                " INNER JOIN " + CurrencySql.TABLE_NAME +
                " ON " + TABLE_NAME + "." + _CURRENCY_ID + " = " +
                CurrencySql.TABLE_NAME + "." + CurrencySql._ID +
                " INNER JOIN " + TransactionSql.TABLE_NAME +
                " ON " + TABLE_NAME + "." + _ID + " = " +
                TransactionSql.TABLE_NAME + "." + TransactionSql._EXPENSE_ID +
                " WHERE " + TABLE_NAME + "." + _GROUP_ID + " = " + groupId +
                " AND " +
                TransactionSql.TABLE_NAME + "." + TransactionSql._AMOUNT + " <= " + 0 +
                " AND " +
                TransactionSql.TABLE_NAME + "." + TransactionSql._SETTLEUP_TRANSACTION +
                " <= " + MyNumbers.booleanToNumber(includeSettleUps) +
                " GROUP BY " + CurrencySql.TABLE_NAME + "." + CurrencySql._NAME +
                ";";

        Cursor cursor = MyDbHelper.getInstance(context)
                .getReadableDatabase()
                .rawQuery(query, null, null);

        double sumAmountInBaseCurrency = 0;
        int iterations = 0;
        while (cursor.moveToNext()) {
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(CurrencySql._QUANTITY));
            int exchangeRate = cursor.getInt(cursor.getColumnIndexOrThrow(CurrencySql._EXCHANGE_RATE));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionSql._AMOUNT));
            sumAmountInBaseCurrency += (amount / quantity * exchangeRate) * -1;
            iterations++;
        }

        CurrencySql sqlCurrency = new CurrencySql();
        String currencyName = sqlCurrency.getCurrency(activeGroupCurrencyId).getName();
        int baseCurrency = sqlCurrency.getBaseCurrency().getId();
        double sumAmount = CurrencyOperations.exchangeAmount(sumAmountInBaseCurrency, baseCurrency, activeGroupCurrencyId);
        if (sumAmount <= DebtCalculator.TOLERANCE) { sumAmount = 0; }

        if (iterations > 0) { cursor.close(); }

        return new SummaryExpenseItem(activeGroupCurrencyId, currencyName, sumAmount);
    }

    private Expense buildExpense(Cursor cursor) {
        return new Expense(
                cursor.getInt(cursor.getColumnIndexOrThrow(_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(_PAYER_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(_GROUP_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(_CURRENCY_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(_REASON)),
                cursor.getString(cursor.getColumnIndexOrThrow(_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(_TIME))
        );
    }
}
