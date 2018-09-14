package cz.milandufek.dluzniceklite;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;

import cz.milandufek.dluzniceklite.models.Currency;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class AddCurrencyOnClickListenerTest {

    @Test
    public void currencyWithEmptyNameShowsWarning() {
        // we set up the right conditions that we want to test
        final Currency currencyWithEmptyName = new Currency(0, "", "b", 1, 1.0, 0, false, true);
        final AddCurrency activity = mock(AddCurrency.class);
        final AddCurrencyOnClickListener tested = new AddCurrencyOnClickListener(activity);
        // spy() from the Mockito framework will allow us to check which methods were called
        final AddCurrencyOnClickListener spied = spy(tested);
        // force this method to be empty, so that we don't need to worry about Android main/worker
        doNothing().when(spied).showText(anyInt());
        spied.onClick(mock(View.class)); // call the code we want to test
        // verify that the was called; if it weren't empty (see above), it would've shown the text
        verify(spied).showText(eq(R.string.warning_currency_empty));
    }

}
