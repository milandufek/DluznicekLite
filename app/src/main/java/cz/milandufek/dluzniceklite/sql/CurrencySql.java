package cz.milandufek.dluzniceklite.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.utils.MyDbHelper;
import cz.milandufek.dluzniceklite.utils.MyNumbers;

public class CurrencySql implements BaseColumns {

    private static final String TAG = "CurrencySql";
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

    public long insertCurrency(Currency currency) {
        ContentValues values = new ContentValues();
        // _ID
        values.put(_NAME, currency.getName());
        values.put(_COUNTRY, currency.getCountry());
        values.put(_QUANTITY, currency.getQuantity());
        values.put(_EXCHANGE_RATE, currency.getExchangeRate());
        values.put(_BASE_CURRENCY, currency.getBaseCurrency());
        values.put(_IS_BASE_CURRENCY, MyNumbers.booleanToNumber(currency.getIsBaseCurrency()));
        values.put(_IS_DELETABLE, MyNumbers.booleanToNumber(currency.getIsDeletable()));

        return MyDbHelper.getInstance(context)
                .getWritableDatabase()
                .insert(TABLE_NAME, null, values);
    }

    public List<Currency> getAllCurrency() {
        Cursor cursor = MyDbHelper.getInstance(context)
                .getReadableDatabase()
                .query(TABLE_NAME, ALL_COLS, null, null, null, null, _ID);

        List<Currency> currency = new ArrayList<>();
        while (cursor.moveToNext()) {
            currency.add(buildCurrency(cursor));
        }

        return currency;
    }

    public Currency getCurrency(int id) {
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = MyDbHelper.getInstance(context)
                .getReadableDatabase()
                .query(TABLE_NAME, ALL_COLS, _ID + " = ?", selectionArgs,
                null, null, null, null);

        Currency currency = null;
        while (cursor.moveToNext()) {
            currency = buildCurrency(cursor);
            cursor.close();
        }

        return currency;
    }

    public Currency getBaseCurrency() {
        String[] selectionArgs = { "1" };

        Cursor cursor = MyDbHelper.getInstance(context)
                .getReadableDatabase()
                .query(TABLE_NAME, ALL_COLS, _IS_BASE_CURRENCY + " = ?", selectionArgs,
                null, null, null, "1");

        Currency currency = null;
        while (cursor.moveToNext()) {
            currency = buildCurrency(cursor);
            cursor.close();
        }

        return currency;
    }

    public int deleteCurrency(int id) {
        String selection = _ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        return MyDbHelper.getInstance(context).getWritableDatabase()
                .delete(TABLE_NAME, selection, selectionArgs);
    }

    public boolean updateCurrency(Currency currency) {
        ContentValues values = new ContentValues();
        values.put(_NAME, currency.getName());
        values.put(_COUNTRY, currency.getCountry());
        values.put(_QUANTITY, currency.getQuantity());
        values.put(_EXCHANGE_RATE, currency.getExchangeRate());
        values.put(_BASE_CURRENCY, currency.getBaseCurrency());
        values.put(_IS_BASE_CURRENCY, currency.getIsBaseCurrency());
        values.put(_IS_DELETABLE, currency.getIsDeletable());

        int update = MyDbHelper.getInstance(context).getWritableDatabase()
                .update(TABLE_NAME, values,
                _ID + " = ? ", new String[] { String.valueOf(currency.getId()) } );

        return update == 1;
    }

    /**
     * Check if currency with @param name exists in the database
     * @param
     *      name CODE
     * @return
     *      true if yes
     */
    public boolean checkIfCurrencyExists(String name) {
        String[] column = { _NAME };
        String selection = _NAME + " = ?";
        String[] selectionArgs = { name };

        Cursor cursor = MyDbHelper.getInstance(context).getReadableDatabase()
                .query(TABLE_NAME, column, selection, selectionArgs,null,null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();

        return exists;
    }

    private Currency buildCurrency(Cursor cursor) {
        return new Currency(
                cursor.getInt(cursor.getColumnIndexOrThrow(_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(_COUNTRY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(_QUANTITY)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(_EXCHANGE_RATE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(_BASE_CURRENCY)),
                MyNumbers.numberToBoolean(cursor.getInt(cursor.getColumnIndexOrThrow(_IS_BASE_CURRENCY))),
                MyNumbers.numberToBoolean(cursor.getInt(cursor.getColumnIndexOrThrow(_IS_DELETABLE)))
        );
    }
}
