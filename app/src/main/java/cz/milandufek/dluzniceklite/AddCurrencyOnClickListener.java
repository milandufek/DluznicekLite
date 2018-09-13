package cz.milandufek.dluzniceklite;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;

class AddCurrencyOnClickListener implements View.OnClickListener {

    private static final String TAG = AddCurrencyOnClickListener.class.toString();

    private final CurrencyRepo sql = new CurrencyRepo();
    private final Currency currency;
    private final AppCompatActivity activity;

    public Currency getCurrency() {
        return currency;
    }

    public AddCurrencyOnClickListener(final Currency currency, final AppCompatActivity activity) {
        this.currency = currency;
        this.activity = activity;
    }

    private void showText(int resourceId, int duration) {
        Toast.makeText(activity, activity.getString(resourceId), duration).show();
    }

    void showText(int resourceId) {
        showText(resourceId, Toast.LENGTH_SHORT);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: save");
        String currencyName = currency.getName();
        String currencyExchangeRate = String.valueOf(currency.getExchangeRate());


        if (currencyName.isEmpty()) {
            showText(R.string.warrning_currency_empty);
        } else if (sql.checkIfCurrencyExists(currencyName)) {
            showText(R.string.warrning_currency_exists);
        } else if (currencyExchangeRate.isEmpty()) { // is this even possible?
            showText(R.string.warrning_exrate_empty);
        } else {
            if (sql.insertCurrency(currency) > 0) {
                showText(R.string.saved);
                // go back to parent activity
                activity.startActivity(activity.getParentActivityIntent());
            } else {
                showText(R.string.error);
            }
        }
    }
}
