package cz.milandufek.dluzniceklite.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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


    private Group buildGroup(Cursor cursor) {
        return new Group(
                cursor.getInt(cursor.getColumnIndexOrThrow(_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(_NAME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(_CURRENCY_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(_DESCRIPTION))
        );
    }

    public long insertGroup(Group group) {
        Log.d(TAG, "insertGroup: inserting");
        ContentValues values = new ContentValues();
        // _ID
        values.put(_NAME, group.getName());
        values.put(_CURRENCY_ID, group.getCurrency());
        values.put(_DESCRIPTION, group.getDescription());

        return MyDbHelper.getInstance(context).getWritableDatabase()
                .insert(TABLE_NAME ,null, values);
    }

    public boolean updateGroup(Group group) {
        ContentValues values = new ContentValues();
        int id = group.getId();
        values.put(_NAME, group.getName());
        values.put(_CURRENCY_ID, group.getCurrency());
        values.put(_DESCRIPTION, group.getDescription());

        int update = MyDbHelper.getInstance(context).getWritableDatabase()
                .update(TABLE_NAME, values,_ID + " = ? ", new String[] { String.valueOf(id) } );

        return update == 1;
    }

    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();

        Cursor cursor =  MyDbHelper.getInstance(context).getReadableDatabase()
                .query(TABLE_NAME, ALL_COLS, null, null, null, null, _ID);

        while (cursor.moveToNext()) {
            groups.add(buildGroup(cursor));
        }
        cursor.close();

        return groups;
    }

    public Group getGroup(int id) {
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = MyDbHelper.getInstance(context).getReadableDatabase()
                .query(TABLE_NAME, ALL_COLS, _ID + " = ?", selectionArgs,
                        null, null, null);

        Group group = null;
        while (cursor.moveToNext()) {
            group = buildGroup(cursor);
            cursor.close();
        }

        return group;
    }

    public int deleteGroup(int id) {
        String selection = _ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        return MyDbHelper.getInstance(context).getWritableDatabase()
                .delete(TABLE_NAME, selection, selectionArgs);
    }
}
