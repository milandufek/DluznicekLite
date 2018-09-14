package cz.milandufek.dluzniceklite;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.utils.CurrencyRVAdapter;

public class ListCurrency extends AppCompatActivity {

    private static final String TAG = "ListCurrency";

    // TODO load data from CNB page
    // http://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.txt?date=22.06.2018

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_currency);

        initRecyclerView();

        // Floating Button
        FloatingActionButton fab = findViewById(R.id.fab_currency_add);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(ListCurrency.this, AddCurrency.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
    }

    /**
     * Setup RecyclerView
     */
    private void initRecyclerView() {
        List<Currency> currencies = new CurrencyRepo().getAllCurrency();

        CurrencyRVAdapter adapter = new CurrencyRVAdapter(this, currencies);

        RecyclerView recyclerView = findViewById(R.id.rv_currency_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
