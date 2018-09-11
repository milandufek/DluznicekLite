package cz.milandufek.dluzniceklite.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.utils.DbHelper;

public class CurrencyRepo implements BaseColumns {

    private Context context;

    public static final String TABLE_NAME = "currencies";
    static final String _NAME = "name";
    private static final String _COUNTRY = "country";
    static final String _QUANTITY = "quantity";
    static final String _EXCHANGE_RATE = "exchange_rate";
    private static final String _BASE_CURRENCY = "base_currency";
    private static final String _IS_BASE_CURRENCY = "is_base_currency";
    private static final String _IS_DELETABLE = "is_deletable";

    private static final String[] ALL_COLS = {_ID, _NAME, _COUNTRY, _QUANTITY,
            _EXCHANGE_RATE, _BASE_CURRENCY, _IS_BASE_CURRENCY, _IS_DELETABLE};

    public static final String CREATE_TABLE_CURRENCY = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            _NAME + " TEXT NOT NULL, " +
            _COUNTRY + " TEXT NOT NULL, " +
            _QUANTITY + " TEXT, " +
            _EXCHANGE_RATE + " REAL NOT NULL, " +
            _BASE_CURRENCY + " INTEGER NOT NULL, " +
            _IS_BASE_CURRENCY + " INTEGER, " +
            _IS_DELETABLE + " INTEGER " +
            ");";

    /**
     * Insert a new row_currency into the database
     *
     * @param currency
     * @return row id or -1 if error
     */
    public long insertCurrency(Currency currency) {
        ContentValues values = new ContentValues();
        // _ID
        values.put(_NAME, currency.getName());
        values.put(_COUNTRY, currency.getCountry());
        values.put(_QUANTITY, currency.getQuantity());
        values.put(_EXCHANGE_RATE, currency.getExchangeRate());
        values.put(_BASE_CURRENCY, currency.getBaseCurrency());
        values.put(_IS_BASE_CURRENCY, currency.getIsBaseCurrency());
        values.put(_IS_DELETABLE, currency.getIsDeletable());

        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * Retrieve all data from row_currency table
     *
     * @return List<Currency>
     */
    public List<Currency> getAllCurrency() {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, ALL_COLS, null, null, null, null, _ID);

        List<Currency> currency = new ArrayList<>();
        while (cursor.moveToNext()) {
            currency.add(new Currency(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getDouble(4),
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getInt(7)));
        }
        cursor.close();

        return currency;
    }

    /**
     * Select currency based on its id
     * @param id
     * @return
     */
    public Currency getCurrency(int id) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_NAME, ALL_COLS, _ID + " = ?", selectionArgs,
                null, null, null, null);

        cursor.moveToFirst();
        Currency currency = new Currency();
        currency.setId(cursor.getInt(0));
        currency.setName(cursor.getString(1));
        currency.setCountry(cursor.getString(2));
        currency.setQuantity(cursor.getInt(3));
        currency.setExchangeRate(cursor.getDouble(4));
        currency.setBaseCurrency(cursor.getInt(5));
        currency.setIsBaseCurrency(cursor.getInt(6));
        currency.setIsDeletable(cursor.getInt(7));

        return currency;
    }

    /**
     * Update a item_currency by given item_currency COL_ID
     * Replace the current row_currency with @param 'contact'
     * @param id
     * @return
     */
    public int deleteCurrency(int id) {
        String selection = _ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        int rows = db.delete(TABLE_NAME, selection, selectionArgs);

        return rows;
    }

    /** TODO update
     * Update a item_currency by given item_currency COL_ID
     * Replace the current row_currency with @param 'contact'
     * @param currency
     * @return
     */
    public boolean updateCurrency(Currency currency) {
        ContentValues values = new ContentValues();
        int id = currency.getId();
        values.put(_NAME, currency.getName());
        values.put(_COUNTRY, currency.getCountry());
        values.put(_QUANTITY, currency.getQuantity());
        values.put(_EXCHANGE_RATE, currency.getExchangeRate());
        values.put(_BASE_CURRENCY, currency.getBaseCurrency());
        values.put(_IS_BASE_CURRENCY, currency.getIsBaseCurrency());
        values.put(_IS_DELETABLE, currency.getIsDeletable());

        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        int update = db.update(TABLE_NAME, values,
                _ID + " = ? ", new String[] { String.valueOf(id) } );

        return update == 1;
    }

    /**
     * Check if currency with @param name exists in the database
     * @param name
     * @return
     */
    public boolean checkIfCurrencyExists(String name) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        String[] column = { _NAME };
        String selection = _NAME + " = ?";
        String[] selectionArgs = { name };

        Cursor cursor = db.query(TABLE_NAME, column, selection, selectionArgs,null,null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();

        return exists;
    }
}
