package cz.milandufek.dluzniceklite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import cz.milandufek.dluzniceklite.models.Currency;

public class AddCurrency extends AppCompatActivity {

    private static final String TAG = AddCurrency.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);

        EditText name = findViewById(R.id.et_currency_name);
        EditText country = findViewById(R.id.et_currency_country);
        EditText quantity = findViewById(R.id.et_currency_quantity);
        EditText exchangeRate = findViewById(R.id.et_currency_exchrate);
        Button btnAdd = findViewById(R.id.btn_currency_add);

        int parsedQuantity = getFieldValueAsInt(R.id.et_currency_quantity);
        double parsedExchangeRate = getFieldValueAsDouble(R.id.et_currency_exchrate);
        Currency currency = new Currency(0, getFieldValue(R.id.et_currency_name),
                getFieldValue(R.id.et_currency_country), parsedQuantity, parsedExchangeRate, 0, false, true);

        // save button
        btnAdd.setOnClickListener(new AddCurrencyOnClickListener(currency, this));
    }

    private String getFieldValue(final int resourceId) {
        return findViewById(resourceId).toString();
    }

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
