package cz.milandufek.dluzniceklite;

import cz.milandufek.dluzniceklite.adapters.TitleSpinnerAdapter;
import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.models.GroupMember;
import cz.milandufek.dluzniceklite.sql.CurrencySql;
import cz.milandufek.dluzniceklite.sql.GroupMemberSql;
import cz.milandufek.dluzniceklite.sql.GroupSql;
import cz.milandufek.dluzniceklite.utils.MyDbHelper;
import cz.milandufek.dluzniceklite.utils.MyPreferences;
import cz.milandufek.dluzniceklite.adapters.SectionsPageAdapter;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyDbHelper.getInstance(getApplicationContext()).getWritableDatabase();

        // SQL explorer plugin
        Stetho.initializeWithDefaults(this);

        // refill SQLite test data on first start
        if (new MyPreferences(this).checkFirstStart()) {
            initCurrencies();
            refillTestData();
        }

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // spinner with group names and set title
        initToolbarTitleSpinner();

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = findViewById(R.id.ll_group_container);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs_main_activity);
        tabLayout.setupWithViewPager(viewPager);

        // Floating Button (add payment)
        FloatingActionButton fab = findViewById(R.id.fab_payment_add);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddExpense.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
    }

    /**
     * Setup view pager for tab fragments
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter sectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        ListExpenseFragment listExpenseFragment = new ListExpenseFragment();
        sectionsPageAdapter.addFragment(listExpenseFragment, getString(R.string.tab_payments));

        SettleUpFragment settleUpFragment = new SettleUpFragment();
        sectionsPageAdapter.addFragment(settleUpFragment, getString(R.string.tab_settleup));

        viewPager.setAdapter(sectionsPageAdapter);
        viewPager.setCurrentItem(0);
    }

    private int getActiveGroupId() {
        MyPreferences sp = new MyPreferences(this);
        return sp.getActiveGroupId();
    }

    private void refreshActivity() {
        Intent newActivity = new Intent(this, MainActivity.class);
        newActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(newActivity);
    }

    private void initCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        currencies.add(new Currency(0, "CZK", "Česká Republika",
                1, 1, 1, true, false));
        currencies.add(new Currency(1, "USD", "USA",
                1, 20, 1, false, true));
        currencies.add(new Currency(2, "EUR", "EMU",
                1, 25, 1, false, true));
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

    private void refillTestData() {
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

        MyPreferences preferences = new MyPreferences(this);
        preferences.setActiveGroupId((int) groupId);
        preferences.setActiveGroupName(groupName);
    }

    private void initToolbarTitleSpinner() {
        List<Group> groups = new GroupSql().getAllGroups();
        if (! groups.isEmpty()) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

            List<String> groupNames = new ArrayList<>();
            List<Integer> groupIds = new ArrayList<>();
            for (Group group : groups) {
                groupIds.add(group.getId());
                groupNames.add(group.getName());
            }

            final TitleSpinnerAdapter groupAdapter = new TitleSpinnerAdapter(this, groupNames);
            Spinner selectTitle = findViewById(R.id.spinner_main_title);
            selectTitle.setAdapter(groupAdapter);
            selectTitle.setSelection(groupIds.indexOf(getActiveGroupId()));

            selectTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    MyPreferences sp = new MyPreferences(getApplicationContext());
                    if (sp.getActiveGroupId() != groupIds.get(position)) {
                        sp.setActiveGroupId(groupIds.get(position));
                        sp.setActiveGroupName(groupNames.get(position));
                        refreshActivity();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }
    }

    /**
     * Inflate the menu
     *
     * @param menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * hHandle activity item from menu
     *
     * @param item
     * @return selected item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        int id = item.getItemId();

        switch (id) {
            case R.id.action_manage_groups:
                intent = new Intent(this, ManageGroups.class);
                break;
            case R.id.action_manage_currencies:
                intent = new Intent(this, ListCurrency.class);
                break;
            case R.id.action_export_data:
                intent = new Intent(this, ExportDataActivity.class);
                break;
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                break;
        }

        if (intent != null) startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
