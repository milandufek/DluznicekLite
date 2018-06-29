package cz.milandufek.dluzniceklite;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.utils.CurrencyRecyclerViewAdapter;

public class ListCurrency extends AppCompatActivity {
    private static final String TAG = "ListCurrency";

    private List<Currency> currency = new ArrayList<>();
    // http://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.txt?date=22.06.2018

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_currency);
        Log.d(TAG, "onCreate: started.");

        // init data
        initData();
        initRecyclerView();

        // Floating Button
        FloatingActionButton fab = findViewById(R.id.fab_currency_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListCurrency.this, AddCurrency.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }

    /**
     *  Load data from database and prepare then to display
     *  Create default one if empty
     */
    private List<Currency> initData() {
        Log.d(TAG, "initData: ");

        Cursor cursor = new CurrencyRepo().getAllCurrency();
        while (cursor.moveToNext()) {
            currency.add(new Currency(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getDouble(4),
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getInt(7)));
        }
        cursor.close();

        return currency;
    }

    /**
     * Setup RecyclerView
     */
    public void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: ");

        RecyclerView recyclerView = findViewById(R.id.rv_currency_list);
        CurrencyRecyclerViewAdapter adapter = new CurrencyRecyclerViewAdapter(this, initData());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
