package cz.milandufek.dluzniceklite.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public final class CurrencyDownloader extends AsyncTask {

    private static final String TAG = "CurrencyDownloader";
    private static final String LINK_MASK = "http://www.cnb.cz/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.txt?date=";

    private Object o;

    public CurrencyDownloader() { }

    public InputStream downloadCurrencyExchangeRates() {
        return (InputStream) doInBackground(null);
    }

    // TODO load data from CNB page

    @Override
    protected Object doInBackground(Object[] objects) {
        String date = MyDateTime.convertDateUsToEu(MyDateTime.getDateToday());

        InputStream in = null;
        try {
            URL url = new URL(LINK_MASK + date);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = httpConnection.getInputStream();
            }
            httpConnection.disconnect();

        } catch (IOException e) {
            Log.e(TAG, "IOException: ", e);
        }

        return in;
    }
}
