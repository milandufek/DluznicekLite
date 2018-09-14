package cz.milandufek.dluzniceklite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;

public class AddCurrency extends AppCompatActivity {

    private static final String TAG = AddCurrency.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);

        // save button
        Button btnAdd = findViewById(R.id.btn_currency_add);
        btnAdd.setOnClickListener(new AddCurrencyOnClickListener(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
