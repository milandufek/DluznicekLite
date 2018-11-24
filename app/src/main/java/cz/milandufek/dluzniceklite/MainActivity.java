package cz.milandufek.dluzniceklite;

import cz.milandufek.dluzniceklite.adapters.TitleSpinnerAdapter;
import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.sql.GroupSql;
import cz.milandufek.dluzniceklite.utils.CsvReader;
import cz.milandufek.dluzniceklite.utils.CurrencyDownloader;
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
            Init init = new Init(getApplicationContext());
            init.initCurrencies();
            init.refillTestData();
        }

        // TEST
//        List cnbCurrencies;
//        cnbCurrencies = new CsvReader(new CurrencyDownloader().downloadCurrencyExchangeRates()).read();
//        Log.d(TAG, "onCreate: cnbCurrencies = " + cnbCurrencies.toString());

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

        switch (item.getItemId()) {
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
