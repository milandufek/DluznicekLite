package cz.milandufek.dluzniceklite;

import cz.milandufek.dluzniceklite.utils.DbHelper;
import cz.milandufek.dluzniceklite.utils.MySharedPreferences;
import cz.milandufek.dluzniceklite.utils.SectionsPageAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DbHelper.getInstance(getApplicationContext()).getWritableDatabase();

        // SQL explorer plugin
        Stetho.initializeWithDefaults(this);

        // init database
        //DbHelper db = new DbHelper(this);

        // init shared preferences
        initSharePreferences();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle(title);
        //toolbar.setBackgroundColor(getColor(R.color.colorPrimaryYelloy));
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the X tabbed activity
        SectionsPageAdapter pageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.ll_group_container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_main_activity);
        tabLayout.setupWithViewPager(mViewPager);

        // Floating Button (add payment)
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_payment_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddExpense.class);
                startActivity(intent);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ListExpense(), getString(R.string.tab_payments));
        //adapter.addFragment(new ListGroups(), getString(R.string.tab_groups));
        adapter.addFragment(new SettleUpActivity(), getString(R.string.tab_settleup));
        viewPager.setAdapter(adapter);
    }

    private void initSharePreferences() {
        MySharedPreferences sp = new MySharedPreferences(this);
        title = sp.getActiveGroupName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * hHandle activity item from menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        int id = item.getItemId();

        switch (id) {
            case R.id.action_manage_groups:
                intent = new Intent(this, ListGroups.class);
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
        initSharePreferences();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}
