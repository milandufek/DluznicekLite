package cz.milandufek.dluzniceklite.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {

    private static final String SHARED_PREFERENCES = "dluznicek";
    private static final String SP_ACTIVE_GID = "active_group_id";
    private static final String SP_ACTIVE_GROUP_NAME = "active_group_name";
    private static final String SP_ACTIVE_CID = "active_group_currency";

    private final SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public MyPreferences(Context context) {
        this.preferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setActiveGroupId(int groupId) {
        editor = preferences.edit();
        editor.putInt(SP_ACTIVE_GID, groupId);
        editor.apply();
    }

    public void setActiveGroupName(String groupName) {
        editor = preferences.edit();
        editor.putString(SP_ACTIVE_GROUP_NAME, groupName);
        editor.apply();
    }

    public void setActiveGroupCurrencyId(int currencyId) {
        editor = preferences.edit();
        editor.putInt(SP_ACTIVE_CID, currencyId);
        editor.apply();
    }

    public int getActiveGroupId() {
        return preferences.getInt(SP_ACTIVE_GID,0);
    }

    public String getActiveGroupName() {
        return preferences.getString(SP_ACTIVE_GROUP_NAME, "");
    }

    public int getActiveGroupCurrencyId() {
        return preferences.getInt(SP_ACTIVE_CID,0);
    }
}
