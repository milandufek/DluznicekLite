package cz.milandufek.dluzniceklite.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.sql.CurrencySql;

public class CurrencyRepo {

    private static final String TAG = "CurrencyRepo";

    private Currency currency;
    private LiveData<List<Currency>> allCurrencies;

    public CurrencyRepo(Application application) {
//        CurrencySql sql = new CurrencySql();
//        allCurrencies = sql.getAllCurrency();
    }
}
