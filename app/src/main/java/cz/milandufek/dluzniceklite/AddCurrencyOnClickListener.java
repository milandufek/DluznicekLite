package cz.milandufek.dluzniceklite;

import android.view.View;
import android.widget.Toast;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;

class AddCurrencyOnClickListener implements View.OnClickListener {

    private static final String TAG = AddCurrencyOnClickListener.class.toString();

    private final CurrencyRepo sql = new CurrencyRepo();
    private final AddCurrency activity;

    AddCurrencyOnClickListener(final AddCurrency activity) {
        this.activity = activity;
    }

    private void showText(int resourceId, int duration) {
        Toast.makeText(activity, activity.getString(resourceId), duration).show();
    }

    private void showText(int resourceId) {
        showText(resourceId, Toast.LENGTH_SHORT);
    }

    @Override
    public void onClick(View v) {
        saveCurrency(activity.getCurrency());
    }

    private void saveCurrency(Currency currency) {
        CurrencyRepo sql = new CurrencyRepo();
        if (currency != null && sql.insertCurrency(currency) > 0) {
            showText(R.string.saved);
            // go back to parent activity
            activity.startActivity(activity.getParentActivityIntent());
        }
    }
}
