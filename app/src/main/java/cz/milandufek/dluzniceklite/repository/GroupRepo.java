package cz.milandufek.dluzniceklite.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.utils.MyDbHelper;

public class GroupRepo implements BaseColumns {
    private static final String TAG = "GroupRepo";

    private Context context;

    public static final String TABLE_NAME = "groups";
    private static final String _NAME = "name";
    private static final String _CURRENCY_ID = "currency_id";
    private static final String _DESCRIPTION = "description";

    private static final String[] ALL_COLS = { _ID, _NAME, _CURRENCY_ID, _DESCRIPTION };

    public static final String CREATE_TABLE_GROUP = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            _NAME + " TEXT NOT NULL, " +
            _CURRENCY_ID + " INTEGER NOT NULL, " +
            _DESCRIPTION + " TEXT, " +
                "FOREIGN KEY(" + _CURRENCY_ID + ") " +
                "REFERENCES " + CurrencyRepo.TABLE_NAME + " (" + CurrencyRepo._ID + ") " +
                "ON DELETE CASCADE " +
            ");";

    /**
     * Insert a new item_group into the database
     * @param group
     * @return row id or -1 if error
     */
    public long insertGroup(Group group) {
        Log.d(TAG, "insertGroup: inserting");
        ContentValues values = new ContentValues();
        // _ID
        values.put(_NAME, group.getName());
        values.put(_CURRENCY_ID, group.getCurrency());
        values.put(_DESCRIPTION, group.getDescription());

        SQLiteDatabase db = MyDbHelper.getInstance(context).getWritableDatabase();
        return db.insert(TABLE_NAME ,null, values);
    }

    /**
     * TODO Update group
     * @param group
     * @return reue if success
     */
    public boolean updateGroup(Group group) {
        ContentValues values = new ContentValues();
        int id = group.getId();
        values.put(_NAME, group.getName());
        values.put(_CURRENCY_ID, group.getCurrency());
        values.put(_DESCRIPTION, group.getDescription());

        SQLiteDatabase db = MyDbHelper.getInstance(context).getWritableDatabase();
        int update = db.update(TABLE_NAME, values,
                _ID + " = ? ", new String[] { String.valueOf(id) } );

        return update == 1;
    }

    /**
     * Select all groups from database
     * @return
     */
    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        SQLiteDatabase db =  MyDbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, ALL_COLS, null, null,
                null, null, _ID);

        Group group;
        while (cursor.moveToNext()) {
            group = new Group(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getString(3)
            );
            groups.add(group);
        }
        cursor.close();

        return groups;
    }

    /**
     * Delete item_group from database
     * @param id
     * @return number of row affected
     */
    public int deleteGroup(int id) {
        String selection = _ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        SQLiteDatabase db = MyDbHelper.getInstance(context).getWritableDatabase();

        return db.delete(TABLE_NAME, selection, selectionArgs);
    }
}
