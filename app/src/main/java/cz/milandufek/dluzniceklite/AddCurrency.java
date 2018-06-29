package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;

public class AddCurrency extends AppCompatActivity {
    private static final String TAG = "AddCurrency";

    private Context context = this;
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
        Button btnAdd = findViewById(R.id.btn_currency_add);

        // save button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: save");
                String currencyName = name.getText().toString();
                String currencyCountry = country.getText().toString();
                String currencyExchangeRate = exchangeRate.getText().toString();
                int quantity;

                if (checkIfStringIsEmpty(currencyName)) {
                    Log.d(TAG, "onClick: item_currency name is empty " + currencyName);
                    Toast.makeText(context,getString(R.string.warrning_currency_empty),
                            Toast.LENGTH_SHORT).show();
                }
                else if (new CurrencyRepo().checkIfCurrencyExists(currencyName)) {
                    Log.d(TAG, "onClick: item_currency already exists " + currencyName);
                    Toast.makeText(context, getString(R.string.warrning_currency_exists),
                            Toast.LENGTH_SHORT).show();
                }
                else if (checkIfStringIsEmpty(currencyExchangeRate)) {
                    Log.d(TAG, "onClick: exchange rate is empty " + currencyName);
                    Toast.makeText(context,getString(R.string.warrning_exrate_empty),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onClick: saving new row - currency " + currencyName);
                    // set quantity
                    quantity = checkIfStringIsEmpty(currencyExchangeRate)
                            ? 1
                            : Integer.parseInt(AddCurrency.this.quantity.getText().toString());
                    CurrencyRepo sql = new CurrencyRepo();
                    Currency currency = new Currency(0, currencyName, currencyCountry, quantity,
                            Double.parseDouble(currencyExchangeRate),
                            0,0, 1);

                    if (sql.insertCurrency(currency) > 0) {
                        Log.d(TAG, "onClick: " + currencyName + " saved");
                        Toast.makeText(context, getString(R.string.saved),
                                Toast.LENGTH_SHORT).show();

                        // go back to parent activity
                        startActivity(getParentActivityIntent());
                    }
                    else {
                        Log.d(TAG, "DB: Cannot insert data...");
                        Toast.makeText(context, getString(R.string.error),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean checkIfStringIsEmpty(String string) {
        return string.equals("");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
