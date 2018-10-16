package cz.milandufek.dluzniceklite;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.sql.CurrencySql;

class CurrencyOnClickListener implements View.OnClickListener {

    private static final String TAG = CurrencyOnClickListener.class.toString();

    static final int ADD  = 1;
    static final int EDIT = 2;

    private AppCompatActivity activity;
    private int action;
    private int currencyId;

    private EditText name, country, quantity, exchangeRate;

    CurrencyOnClickListener(AppCompatActivity activity, int action) {
        this.activity = activity;
        this.action   = action;
        this.currencyId = 0;
    }

    CurrencyOnClickListener(AppCompatActivity activity, int action, int currencyId) {
        this.activity = activity;
        this.action   = action;
        this.currencyId = currencyId;
    }

    @Override
    public void onClick(View v) {
        name = activity.findViewById(R.id.et_currency_name);
        country = activity.findViewById(R.id.et_currency_country);
        quantity = activity.findViewById(R.id.et_currency_quantity);
        exchangeRate = activity.findViewById(R.id.et_currency_exchrate);

        switch (action) {
            case ADD:
                addCurrency(getCurrency());
                break;
            case EDIT:
                editCurrency(getCurrency());
                break;
        }
    }

    public Currency getCurrency() {
        String currencyName = getFieldValue(name);
        String currencyCountry = getFieldValue(country);
        String currencyExchangeRate = getFieldValue(exchangeRate);
        String currencyQuantity = getFieldValue(quantity);

        Currency currency = null;
        if (currencyName.isEmpty()) {
            showText(R.string.warning_currency_empty);
        } else if (currencyName.length() < 3) {
            showText(R.string.warning_code_not_3_chars);
        } else if (action == ADD && new CurrencySql().checkIfCurrencyExists(currencyName)) {
            showText(R.string.warning_currency_exists);
        } else if (currencyExchangeRate.isEmpty()) {
            showText(R.string.warning_exrate_empty);
        } else {
            if (currencyQuantity.isEmpty())
                currencyQuantity = "1";
            currency = new Currency(currencyId, currencyName, currencyCountry,
                    Integer.valueOf(currencyQuantity),
                    Double.parseDouble(currencyExchangeRate),
                    0,false, true);
        }

        return currency;
    }

    private void addCurrency(Currency currency) {
        CurrencySql sql = new CurrencySql();
        if (currency != null && sql.insertCurrency(currency) > 0) {
            showText(R.string.saved);
            goBackToParentActivity();
        }
    }

    private void editCurrency(Currency currency) {
        CurrencySql sql = new CurrencySql();
        if (currency != null && sql.updateCurrency(currency)) {
            showText(R.string.saved);
            goBackToParentActivity();
        }
    }

    public void showText(int resourceId, int duration) {
        Toast.makeText(activity, activity.getString(resourceId), duration).show();
    }
    public void showText(int resourceId) {
        showText(resourceId, Toast.LENGTH_SHORT);
    }

    private String getFieldValue(final EditText field) {
        return field.getText().toString();
    }

    private void goBackToParentActivity() {
        activity.startActivity(activity.getParentActivityIntent());
        activity.finish();
    }
}
