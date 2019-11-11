package cz.milandufek.dluzniceklite;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.sql.CurrencySql;
import cz.milandufek.dluzniceklite.adapters.CurrencyRVAdapter;

public class ListCurrency extends AppCompatActivity {

    private static final String TAG = "ListCurrency";

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

    private void initRecyclerView() {
        List<Currency> currencies = new CurrencySql().getAllCurrencies();

        CurrencyRVAdapter adapter = new CurrencyRVAdapter(this, currencies);

        RecyclerView recyclerView = findViewById(R.id.rv_currency_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
