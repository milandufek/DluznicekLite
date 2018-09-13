package cz.milandufek.dluzniceklite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import cz.milandufek.dluzniceklite.models.Currency;

public class AddCurrency extends AppCompatActivity {

    private static final String TAG = AddCurrency.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);

        name = findViewById(R.id.et_currency_name);
        country = findViewById(R.id.et_currency_country);
        quantity = findViewById(R.id.et_currency_quantity);
        exchangeRate = findViewById(R.id.et_currency_exchrate);
        Button btnAdd = findViewById(R.id.btn_currency_add);

        int parsedQuantity = getFieldValueAsInt(R.id.et_currency_quantity);
        double parsedExchangeRate = getFieldValueAsDouble(R.id.et_currency_exchrate);
        Currency currency = new Currency(0, getFieldValue(R.id.et_currency_name),
                getFieldValue(R.id.et_currency_country), parsedQuantity, parsedExchangeRate, 0, 0, 1);

        // save button
        btnAdd.setOnClickListener(new AddCurrencyOnClickListener(currency, this));
    }


    private String getFieldValue(final int resourceId) {
        return findViewById(resourceId).toString();
    }
                if (checkIfStringIsEmpty(currencyName)) {
                    Toast.makeText(context,getString(R.string.warning_currency_empty),
                            Toast.LENGTH_SHORT).show();
                }
                else if (new CurrencyRepo().checkIfCurrencyExists(currencyName)) {
                    Toast.makeText(context, getString(R.string.warning_currency_exists),
                            Toast.LENGTH_SHORT).show();
                }
                else if (checkIfStringIsEmpty(currencyExchangeRate)) {
                    Toast.makeText(context,getString(R.string.warning_exrate_empty),
                            Toast.LENGTH_SHORT).show();
                } else {
                    quantity = checkIfStringIsEmpty(currencyExchangeRate)
                            ? 1
                            : Integer.parseInt(AddCurrency.this.quantity.getText().toString());
                    CurrencyRepo sql = new CurrencyRepo();
                    Currency currency = new Currency(0, currencyName, currencyCountry, quantity,
                            Double.parseDouble(currencyExchangeRate),
                            0,false, true);

    private int getFieldValueAsInt(final int resourceId) {
        return Integer.parseInt(getFieldValue(resourceId));
    }

    private double getFieldValueAsDouble(final int resourceId) {
        return Double.parseDouble(getFieldValue(resourceId));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
