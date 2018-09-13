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

        name = findViewById(R.id.et_currency_name);
        country = findViewById(R.id.et_currency_country);
        quantity = findViewById(R.id.et_currency_quantity);
        exchangeRate = findViewById(R.id.et_currency_exchrate);
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

                    if (sql.insertCurrency(currency) > 0) {
                        Toast.makeText(context, getString(R.string.saved),
                                Toast.LENGTH_SHORT).show();
                        // go back to parent activity
                        startActivity(getParentActivityIntent());
                    }
                    else {
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
