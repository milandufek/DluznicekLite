package cz.milandufek.dluzniceklite.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private static final String SHARED_PREFERENCES = "dluznicek";
    private static final String SP_ACTIVE_GID = "active_group_id";
    private static final String SP_ACTIVE_GROUP_NAME = "active_group_name";
    private static final String SP_ACTIVE_CID = "active_group_currency";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;

    public MySharedPreferences(Context context) {
        this.sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setActiveGroupId(int groupId) {
        spEditor = sharedPreferences.edit();
        spEditor.putInt(SP_ACTIVE_GID, groupId);
        spEditor.apply();
    }

    public void setActiveGroupName(String groupName) {
        spEditor = sharedPreferences.edit();
        spEditor.putString(SP_ACTIVE_GROUP_NAME, groupName);
        spEditor.apply();
    }

    public void setActiveGroupCurrencyId(int currencyId) {
        spEditor = sharedPreferences.edit();
        spEditor.putInt(SP_ACTIVE_CID, currencyId);
        spEditor.apply();
    }

    public int getActiveGroupId() {
        return sharedPreferences.getInt(SP_ACTIVE_GID,0);
    }

    public String getActiveGroupName() {
        return sharedPreferences.getString(SP_ACTIVE_GROUP_NAME, "");
    }

    public int getActiveGroupCurrency() {
        return sharedPreferences.getInt(SP_ACTIVE_CID,0);
    }
}
