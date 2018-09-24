package cz.milandufek.dluzniceklite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;

public class EditCurrency extends AppCompatActivity {

    private static final String TAG = EditCurrency.class.toString();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);

        int id = getIntent().getExtras().getInt("CURRENCY_ID");

        Currency currency = new CurrencyRepo().getCurrency(id);

        EditText name = findViewById(R.id.et_currency_name);
        EditText country = findViewById(R.id.et_currency_country);
        EditText quantity = findViewById(R.id.et_currency_quantity);
        EditText exchangeRate = findViewById(R.id.et_currency_exchrate);

        name.setText(currency.getName());
        country.setText(currency.getCountry());
        quantity.setText(String.valueOf(currency.getQuantity()));
        exchangeRate.setText(String.valueOf(currency.getExchangeRate()));

        Button btnAdd = findViewById(R.id.btn_currency_add);
        btnAdd.setOnClickListener(new EditCurrencyOnClickListener(this));
    }
}
