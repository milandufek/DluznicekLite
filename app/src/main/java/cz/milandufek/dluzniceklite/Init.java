package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.models.GroupMember;
import cz.milandufek.dluzniceklite.sql.CurrencySql;
import cz.milandufek.dluzniceklite.sql.GroupMemberSql;
import cz.milandufek.dluzniceklite.sql.GroupSql;
import cz.milandufek.dluzniceklite.utils.MyPreferences;

class Init {

    private static final String TAG = "Init";

    private Context context;

    Init(Context context) {
        this.context = context;
    }

    void initCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        currencies.add(new Currency(0, "CZK", "Česká Republika",
                1, 1, 1, true, false));
        currencies.add(new Currency(1, "USD", "USA",
                1, 22.9, 1, false, true));
        currencies.add(new Currency(2, "EUR", "EMU",
                1, 26, 1, false, true));
        currencies.add(new Currency(3, "DNG", "Vietnam",
                1000, 0.97, 1, false, true));
        currencies.add(new Currency(4, "LKR", "Srí Lanka",
                7, 7, 1, false, true));
        currencies.add(new Currency(5, "GPB", "Velká Británie",
                1, 29.5, 1, false, true));
        currencies.add(new Currency(6, "ISK", "Island",
                100, 20.5, 1, false, true));
        CurrencySql currencySql = new CurrencySql();
        for (Currency currency : currencies) {
            long result = currencySql.insertCurrency(currency);
            Log.d(TAG, "initCurrencies: inserting currency ID: " + result);
        }
    }

    void refillTestData() {
        GroupSql groupSql = new GroupSql();
        GroupMemberSql groupMemberSql = new GroupMemberSql();

        // delete all groups
        List<Group> groups = groupSql.getAllGroups();
        for (Group group : groups) {
            groupSql.deleteGroup(group.getId());
        }

        // insert groups & members
        String groupName = "Mololockove";
        long groupId = groupSql.insertGroup(new Group(0, groupName, 2, "Mock mock"));
        String[] groupMembers = {"Mock", "Lock", "Nock", "Clock", "Qock", "Lolock"};
        Log.d(TAG, "refillTestData: group inserted: id = " + groupId);
        for (String member : groupMembers) {
            GroupMember groupMember = new GroupMember(0, (int) groupId, member, null, null, null, false);
            groupMemberSql.insertGroupMember(groupMember);
        }

        MyPreferences preferences = new MyPreferences(context);
        preferences.setActiveGroupId((int) groupId);
        preferences.setActiveGroupName(groupName);
    }
}
