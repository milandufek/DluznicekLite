package cz.milandufek.dluzniceklite;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.models.Expense;
import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.models.GroupMember;
import cz.milandufek.dluzniceklite.models.Transaction;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;
import cz.milandufek.dluzniceklite.repository.GroupMemberRepo;
import cz.milandufek.dluzniceklite.repository.GroupRepo;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;
import cz.milandufek.dluzniceklite.utils.DbHelper;
import cz.milandufek.dluzniceklite.utils.MyDateTime;
import cz.milandufek.dluzniceklite.utils.MyPreferences;
import cz.milandufek.dluzniceklite.utils.SectionsPageAdapter;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DbHelper.getInstance(getApplicationContext()).getWritableDatabase();

        // SQL explorer plugin
        Stetho.initializeWithDefaults(this);

        // refill SQLite test data
        //refillTestData();

        // set active group name
        String title = getActiveGroupName();

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the X tabbed activity
        SectionsPageAdapter pageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.ll_group_container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs_main_activity);
        tabLayout.setupWithViewPager(mViewPager);

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
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        ListExpenseFragment listExpenseFragment = new ListExpenseFragment();
        adapter.addFragment(listExpenseFragment, getString(R.string.tab_payments));

        SettleUpFragment settleUpFragment = new SettleUpFragment();
        adapter.addFragment(settleUpFragment, getString(R.string.tab_settleup));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    /**
     * Get active from name from SharedPreferences
     * @return String
     */
    private String getActiveGroupName() {
        MyPreferences sp = new MyPreferences(this);
        return sp.getActiveGroupName();
    }

    private void initCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        currencies.add(new Currency(0,"CZK","Česká Republika",
                1,1,1,true,false));
        currencies.add(new Currency(1,"USD","USA",
                1,20,1,false,true));
        currencies.add(new Currency(2,"EUR","EMU",
                1,25,1,false,true));
        currencies.add(new Currency(3,"DNG","Vietnam",
                1000,0.97,1,false,true));
        currencies.add(new Currency(4,"LKR","Srí Lanka",
                7,7,1,false,true));
        currencies.add(new Currency(5,"GPB","Velká Británie",
                1,29.5,1,false,true));
        currencies.add(new Currency(6,"ISK","Island",
                100,20.5,1,false,true));
        CurrencyRepo currencyRepo = new CurrencyRepo();
        for (int i = 0; currencies.size() > i; i++) {
            currencyRepo.insertCurrency(currencies.get(i));
        }
    }

    private void refillTestData() {
        GroupRepo groupRepo = new GroupRepo();
        GroupMemberRepo groupMemberRepo = new GroupMemberRepo();
        ExpenseRepo expenseRepo = new ExpenseRepo();
        TransactionRepo transactionRepo = new TransactionRepo();

        // delete all groups
        List<Group> groups = groupRepo.getAllGroups();
        for (Group group : groups) {
            groupRepo.deleteGroup(group.getId());
        }

        // insert groups & members
        long groupId = groupRepo.insertGroup(new Group(0,"Mololockove", 2, "lol"));
        String[] groupMembers = { "Mock", "Lock", "Lolok", "Clock", "Qock" };
        for (String member : groupMembers) {
            GroupMember groupMember = new GroupMember(0, (int) groupId, member, null, null, null, false);
            long memberId = groupMemberRepo.insertGroupMember(groupMember);
        }

        // insert expense & transactions
        Expense expense = new Expense(0,
                1, (int) groupId, 1, "Večeře",
                MyDateTime.getDateToday(), MyDateTime.getDateToday());
        long expenseId = expenseRepo.insertExpense(expense);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(0, 1, (int) expenseId, 500));
        transactions.add(new Transaction(0,2, (int) expenseId, 500));
        transactions.add(new Transaction(0,3, (int) expenseId, 500));
        transactions.add(new Transaction(0,4, (int) expenseId, 500));
        transactions.add(new Transaction(0,5, (int) expenseId, 500));
        transactionRepo.insertTransactions(transactions);
        transactions.clear();

        expense = new Expense(0,
                1, (int) groupId, 2, "Oběd",
                MyDateTime.getDateToday(), MyDateTime.getDateToday());
        expenseId = expenseRepo.insertExpense(expense);
        transactions = new ArrayList<>();
        transactions.add(new Transaction(0,2, (int) expenseId, 50));
        transactions.add(new Transaction(0,3, (int) expenseId, 50));
        transactions.add(new Transaction(0,4, (int) expenseId, 50));
        transactions.add(new Transaction(0,5, (int) expenseId, 50));
        transactionRepo.insertTransactions(transactions);
        transactions.clear();

        expense = new Expense(0,
                1, (int) groupId, 3, "Snídaně",
                MyDateTime.getDateToday(), MyDateTime.getDateToday());
        expenseId = expenseRepo.insertExpense(expense);
        transactions = new ArrayList<>();
        transactions.add(new Transaction(0, 1, (int) expenseId, 50));
        transactions.add(new Transaction(0,2, (int) expenseId, 50));
        transactions.add(new Transaction(0,5, (int) expenseId, 50));
        transactionRepo.insertTransactions(transactions);
        transactions.clear();
    }

    /**
     * Inflate the menu
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
        getActiveGroupName();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}
