package cz.milandufek.dluzniceklite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;

public class AddCurrency extends AppCompatActivity {

    private static final String TAG = AddCurrency.class.toString();
    private EditText name, country, quantity, exchangeRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);

        name = (EditText) findViewById(R.id.et_currency_name);
        country = (EditText) findViewById(R.id.et_currency_country);
        quantity = (EditText) findViewById(R.id.et_currency_quantity);
        exchangeRate = (EditText) findViewById(R.id.et_currency_exchrate);

        // save button
        Button btnAdd = findViewById(R.id.btn_currency_add);
        btnAdd.setOnClickListener(new AddCurrencyOnClickListener(this));
    }

    public Currency getCurrency() {
        String currencyName = getFieldValue(name);
        String currencyCountry = country.getText().toString();
        String currencyExchangeRate = exchangeRate.getText().toString();
        String currencyQuantity = quantity.getText().toString();

        Currency currency = null;
        if (currencyName.isEmpty()) {
            showText(R.string.warning_currency_empty);
        } else if (new CurrencyRepo().checkIfCurrencyExists(currencyName)) {
            showText(R.string.warning_currency_exists);
        } else if (currencyExchangeRate.isEmpty()) {
            showText(R.string.warning_exrate_empty);
        } else {
            if (currencyQuantity.isEmpty())
                currencyQuantity = "1";
            currency = new Currency(0, currencyName, currencyCountry,
                    Integer.valueOf(currencyQuantity),
                    Double.parseDouble(currencyExchangeRate),
                    0,false, true);
        }

        return currency;
    }

    private void showText(int resourceId, int duration) {
        Toast.makeText(this, getString(resourceId), duration).show();
    }
    private void showText(int resourceId) {
        showText(resourceId, Toast.LENGTH_SHORT);
    }

    private String getFieldValue(final EditText field) {
        return field.getText().toString();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
