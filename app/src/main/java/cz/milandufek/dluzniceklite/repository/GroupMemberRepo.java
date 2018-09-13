package cz.milandufek.dluzniceklite.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.GroupMember;
import cz.milandufek.dluzniceklite.utils.DbHelper;
import cz.milandufek.dluzniceklite.utils.MyNumbers;

public class GroupMemberRepo implements BaseColumns {
    private static final String TAG = "GroupMemberRepo";

    private Context context;

    public static final String TABLE_NAME = "group_members";
    static final String _GROUP_ID = "group_id";
    static final String _NAME = "name";
    private static final String _EMAIL = "email";
    private static final String _CONTACT = "contact";
    private static final String _DESCRIPTION = "description";
    private static final String _IS_ME = "is_me";

    private static final String ALL_COLS[] = { _ID, _GROUP_ID, _NAME, _EMAIL,
            _CONTACT, _DESCRIPTION, _IS_ME};

    public static final String CREATE_TABLE_GROUP_MEMBER = "CREATE TABLE " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            _GROUP_ID + " INTEGER NOT NULL," +
            _NAME + " TEXT NOT NULL," +
            _EMAIL + " TEXT," +
            _CONTACT + " TEXT," +
            _DESCRIPTION + " TEXT," +
            _IS_ME + " INTEGER, " +
                "FOREIGN KEY ( " + _GROUP_ID + ") " +
                "REFERENCES " + GroupRepo.TABLE_NAME + "(" + GroupRepo._ID + ") " +
                "ON DELETE CASCADE " +
            ")";

    /**
     * Insert group member into database
     * @param groupMember
     * @return row id
     */
    public long insertGroupMember(GroupMember groupMember) {
        ContentValues values = new ContentValues();
        // _ID
        values.put(_GROUP_ID, groupMember.getGroupId());
        values.put(_NAME, groupMember.getName());
        values.put(_EMAIL, groupMember.getName());
        values.put(_CONTACT, groupMember.getContact());
        values.put(_DESCRIPTION, groupMember.getDescription());
        values.put(_IS_ME, groupMember.getIsMe());

        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        return db.insert(TABLE_NAME,null, values);
    }

    /**
     * Insert multiple group member into database
     * @param groupMembers
     * @return number of rows affected
     */
    public long insertGroupMembers(List<GroupMember> groupMembers) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
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
                values.put(_IS_ME, groupMembers.get(i).getIsMe());

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

    /**
     * Retrieve all data
     * @param groupId
     * @return List of group members
     */
    public List<GroupMember> selectGroupMembers(int groupId) {
        String selection = _GROUP_ID + " = ?";
        String[] selectionArgs = { String.valueOf(groupId) };

        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, ALL_COLS, selection, selectionArgs,
                null, null, _NAME);

        List<GroupMember> members = new ArrayList<>();
        while (cursor.moveToNext()) {
            GroupMember member = new GroupMember(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                MyNumbers.numberToBoolean(cursor.getInt(6))
            );
            members.add(member);
        }
        cursor.close();

        return members;
    }

    /** TODO update
     * Update a groupMember by given id ID
     * Replace the groupMember row with @param
     * @param groupMember
     * @return true if success
     */
    public boolean updateGroupMember(GroupMember groupMember) {
        String selection = _ID + " = ?";
        int id = groupMember.getId();
        String[] selectionArgs = { String.valueOf(id) };

        ContentValues values = new ContentValues();
        // _ID
        values.put(_GROUP_ID, groupMember.getGroupId());
        values.put(_NAME, groupMember.getName());
        values.put(_EMAIL, groupMember.getName());
        values.put(_CONTACT, groupMember.getContact());
        values.put(_DESCRIPTION, groupMember.getDescription());
        values.put(_IS_ME, groupMember.getIsMe());

        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        int update = db.update(TABLE_NAME, values, selection, selectionArgs);

        return update == 1;
    }

    /**
     * Delete item group member from database
     * @param id
     * @return
     */
    public int deleteGroupMember(int id) {
        String selection = _ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        return db.delete(TABLE_NAME, selection, selectionArgs);
    }
}
