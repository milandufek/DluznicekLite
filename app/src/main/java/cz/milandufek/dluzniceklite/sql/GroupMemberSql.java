package cz.milandufek.dluzniceklite.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.GroupMember;
import cz.milandufek.dluzniceklite.utils.MyDbHelper;
import cz.milandufek.dluzniceklite.utils.MyNumbers;

public class GroupMemberSql implements BaseColumns {
    private static final String TAG = "GroupMemberSql";

    private Context context;

    public static final String TABLE_NAME = "group_members";
    static final String _GROUP_ID = "group_id";
    static final String _NAME = "name";
    private static final String _EMAIL = "email";
    private static final String _CONTACT = "contact";
    private static final String _DESCRIPTION = "description";
    private static final String _ACTIVE_PAYMETS = "is_me";

    private static final String ALL_COLS[] = { _ID, _GROUP_ID, _NAME, _EMAIL,
            _CONTACT, _DESCRIPTION, _ACTIVE_PAYMETS};

    public static final String CREATE_TABLE_GROUP_MEMBER = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            _GROUP_ID + " INTEGER NOT NULL," +
            _NAME + " TEXT NOT NULL," +
            _EMAIL + " TEXT," +
            _CONTACT + " TEXT," +
            _DESCRIPTION + " TEXT," +
            _ACTIVE_PAYMETS + " INTEGER, " +
                "FOREIGN KEY ( " + _GROUP_ID + ") " +
                "REFERENCES " + GroupSql.TABLE_NAME + "(" + GroupSql._ID + ") " +
                "ON DELETE CASCADE " +
            ")";

    public long insertGroupMember(GroupMember groupMember) {
        ContentValues values = new ContentValues();
        // _ID
        values.put(_GROUP_ID, groupMember.getGroupId());
        values.put(_NAME, groupMember.getName());
        values.put(_EMAIL, groupMember.getName());
        values.put(_CONTACT, groupMember.getContact());
        values.put(_DESCRIPTION, groupMember.getDescription());
        values.put(_ACTIVE_PAYMETS, groupMember.getActivePayments());

        return MyDbHelper.getInstance(context).getWritableDatabase()
                .insert(TABLE_NAME,null, values);
    }

    public long insertGroupMembers(List<GroupMember> groupMembers) {
        SQLiteDatabase db = MyDbHelper.getInstance(context).getWritableDatabase();
        int rowsAffected = 0;
        int membersCount = groupMembers.size();
        try {
            db.beginTransaction();
            for (int i = 0; membersCount > i; i++) {
                ContentValues values = new ContentValues();
                // _ID
                values.put(_GROUP_ID, groupMembers.get(i).getGroupId());
                values.put(_NAME, groupMembers.get(i).getName());
                values.put(_EMAIL, groupMembers.get(i).getEmail());
                values.put(_CONTACT, groupMembers.get(i).getContact());
                values.put(_DESCRIPTION, groupMembers.get(i).getDescription());
                values.put(_ACTIVE_PAYMETS, groupMembers.get(i).getActivePayments());

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

        if (membersCount == rowsAffected) {
            return rowsAffected;
        } else {
            return -1;
        }
    }

    public List<GroupMember> getAllGroupMembers(int groupId) {
        String selection = _GROUP_ID + " = ?";
        String[] selectionArgs = { String.valueOf(groupId) };

        Cursor cursor = MyDbHelper.getInstance(context).getReadableDatabase()
                .query(TABLE_NAME, ALL_COLS, selection, selectionArgs,
                null, null, _NAME);

        List<GroupMember> members = new ArrayList<>();
        while (cursor.moveToNext()) {
            GroupMember member = new GroupMember(
                cursor.getInt(cursor.getColumnIndexOrThrow(_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(_GROUP_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(_CONTACT)),
                cursor.getString(cursor.getColumnIndexOrThrow(_DESCRIPTION)),
                MyNumbers.numberToBoolean(cursor.getInt(cursor.getColumnIndexOrThrow(_ACTIVE_PAYMETS)))
            );
            members.add(member);
        }
        cursor.close();

        return members;
    }

    public boolean updateGroupMember(GroupMember groupMember) {
        String selection = _ID + " = ?";
        int id = groupMember.getId();
        String[] selectionArgs = { String.valueOf(id) };

        ContentValues values = new ContentValues();
        values.put(_GROUP_ID, groupMember.getGroupId());
        values.put(_NAME, groupMember.getName());
        values.put(_EMAIL, groupMember.getName());
        values.put(_CONTACT, groupMember.getContact());
        values.put(_DESCRIPTION, groupMember.getDescription());
        values.put(_ACTIVE_PAYMETS, groupMember.getActivePayments());

        int update = MyDbHelper.getInstance(context).getWritableDatabase()
                .update(TABLE_NAME, values, selection, selectionArgs);

        return update == 1;
    }

    public int deleteGroupMember(int id) {
        String selection = _ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        return MyDbHelper.getInstance(context).getWritableDatabase()
                .delete(TABLE_NAME, selection, selectionArgs);
    }
}
