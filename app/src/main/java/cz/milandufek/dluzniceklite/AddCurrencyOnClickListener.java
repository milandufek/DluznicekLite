package cz.milandufek.dluzniceklite;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;

class AddCurrencyOnClickListener implements View.OnClickListener {

    private static final String TAG = AddCurrencyOnClickListener.class.toString();

    private final CurrencyRepo sql = new CurrencyRepo();
    private final AddCurrency activity;

    private EditText name, country, quantity, exchangeRate;

    AddCurrencyOnClickListener(final AddCurrency activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        name = activity.findViewById(R.id.et_currency_name);
        country = activity.findViewById(R.id.et_currency_country);
        quantity = activity.findViewById(R.id.et_currency_quantity);
        exchangeRate = activity.findViewById(R.id.et_currency_exchrate);

        saveCurrency(getCurrency());
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

    private void saveCurrency(Currency currency) {
        CurrencyRepo sql = new CurrencyRepo();
        if (currency != null && sql.insertCurrency(currency) > 0) {
            showText(R.string.saved);
            // go back to parent activity
            activity.startActivity(activity.getParentActivityIntent());
            activity.finish();
        }
    }

    private void showText(int resourceId, int duration) {
        Toast.makeText(activity, activity.getString(resourceId), duration).show();
    }
    private void showText(int resourceId) {
        showText(resourceId, Toast.LENGTH_SHORT);
    }

    private String getFieldValue(final EditText field) {
        return field.getText().toString();
    }
}
