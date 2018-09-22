package cz.milandufek.dluzniceklite.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;
import cz.milandufek.dluzniceklite.repository.GroupMemberRepo;
import cz.milandufek.dluzniceklite.repository.GroupRepo;

public class MyDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyDbHelper";

    private final Context context;
    private static MyDbHelper instance = null;

    private static final String DATABASE_NAME = "dluznicek";
    private static final int DATABASE_VERSION = 1;

    /**
     * Get DB instance as a singleton
     * @param context
     * @return MyDbHelper instance
     */
    public synchronized static MyDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MyDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Private constructor  to prevent direct instantiation.
     * Make a call to static method "getInstance()" instead.
     */
    private MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: creating all tables");

        db.execSQL(CurrencyRepo.CREATE_TABLE_CURRENCY);
        db.execSQL(GroupRepo.CREATE_TABLE_GROUP);
        db.execSQL(GroupMemberRepo.CREATE_TABLE_GROUP_MEMBER);
        db.execSQL(ExpenseRepo.CREATE_TABLE_EXPENSE);
        db.execSQL(TransactionRepo.CREATE_TABLE_TRANSACTION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        setWriteAheadLoggingEnabled(true);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onCreate: re-creating all tables");
        // Drop table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + CurrencyRepo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GroupRepo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GroupMemberRepo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseRepo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TransactionRepo.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
