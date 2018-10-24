package cz.milandufek.dluzniceklite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class AddCurrency extends AppCompatActivity {

    private static final String TAG = AddCurrency.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);

        // save button
        Button btnAdd = findViewById(R.id.btn_currency_add);
        btnAdd.setOnClickListener(new CurrencyOnClickListener(this, CurrencyOnClickListener.ADD));
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_save_btn, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        item.setOnMenuItemClickListener(new CurrencyOnClickListener(this, CurrencyOnClickListener.ADD));
//        return true;
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
