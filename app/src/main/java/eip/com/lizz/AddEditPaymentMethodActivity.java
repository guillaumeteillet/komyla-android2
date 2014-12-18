package eip.com.lizz;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;


public class AddEditPaymentMethodActivity extends ActionBarActivity {

    private static final int MY_SCAN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_payment_method);
    }

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);

        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String card_number = null, card_number_hide = null, card_number2 = null, cvv = null;
        int expiryMonth = 0, expityYear = 0;

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                card_number = scanResult.getFormattedCardNumber();
                card_number_hide = scanResult.getRedactedCardNumber();
                card_number2 = scanResult.cardNumber;
                expiryMonth = scanResult.expiryMonth;
                expityYear = scanResult.expiryYear;
                cvv = scanResult.cvv;
            }
            else {
                 // Scan was canceled
            }

            /* DEBUG POUR MATHIEU
             *
              * A supprimer avant la mise en prod :D
              *
              * */

            Log.d("TEST", card_number); //1234 1234 1234 1234
            Log.d("TEST", card_number_hide);// •••• •••• •••• 1234
            Log.d("TEST", card_number2); //1234123412341234
            Log.d("TEST", ""+expiryMonth); // 4
            Log.d("TEST", ""+expityYear); // 2014
            Log.d("TEST", cvv); // 123
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.main_menu(item, getBaseContext());
    }
}
