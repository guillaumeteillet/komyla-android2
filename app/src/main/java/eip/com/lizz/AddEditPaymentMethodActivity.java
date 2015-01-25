package eip.com.lizz;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Calendar;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

//TODO: Mathieu
/*
2) Mettre la verification du numéro de la carte dans une fonction "onTextUpdate" ou qqch comme ça pour éviter le bug de
la validation erroné quand on misclic avant le scan.
3) Désactiver le scan si pas d'APN dispo
*/

public class AddEditPaymentMethodActivity extends ActionBarActivity {

    private static final int MY_SCAN_REQUEST_CODE = 1;

    // XML Attributes
    private EditText edittextCardNumber = null;
    private EditText edittextExpirationDateMonth = null;
    private EditText edittextExpirationDateYear = null;
    private EditText edittextCryptogram = null;
    private EditText edittextOwnerName = null;
    private EditText edittextDisplayName = null;
    private Button saveCard = null;
    private ImageButton scanCard = null;

    String cardNumberStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_payment_method);

        buttonBinding();
        configureEdittextCardNumber();
        configureEdittextExpirationDate();

        saveCard.setEnabled(false);
        // Mathieu : Si l'appareil photo n'existe pas, rendre le scan de carte bancaire impossible.
        PackageManager pm = getBaseContext().getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            scanCard.setVisibility(View.GONE);
        }
    }

    private void configureEdittextExpirationDate() {
        edittextExpirationDateMonth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkExpirationDate();
                }
            }
        });

        edittextExpirationDateYear.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkExpirationDate();
                }
            }
        });
    }

    private void checkExpirationDate() {
        if (edittextExpirationDateMonth.getText().length() != 0 && edittextExpirationDateYear.getText().length() != 0) {

            Log.d("WTF", "Mois -> " + edittextExpirationDateMonth.getText().length());
            Log.d("WTF", "Année -> " + edittextExpirationDateYear.getText().length());

            if (edittextExpirationDateMonth.getText().length() != 2 || edittextExpirationDateYear.getText().length() != 2)
            {
                edittextExpirationDateMonth.setTextColor(Color.RED);
                edittextExpirationDateYear.setTextColor(Color.RED);
                edittextExpirationDateYear.setError("Veuillez respecter le format suivant MM / AA");
            }
            else
            {
                int monthInput = 0;
                int yearInput = 0;

                // Récupération du mois
                if (edittextExpirationDateMonth.getText().charAt(0) == '0') {
                    monthInput += Character.getNumericValue(edittextExpirationDateMonth.getText().charAt(1));
                }
                else if (edittextExpirationDateMonth.getText().charAt(0) == '1') {
                    monthInput += Character.getNumericValue(edittextExpirationDateMonth.getText().charAt(0));
                    monthInput *= 10;
                    monthInput += Character.getNumericValue(edittextExpirationDateMonth.getText().charAt(1));
                }

                // Récupération de l'année
                if (edittextExpirationDateYear.getText().length() == 2) {
                    yearInput += Character.getNumericValue(edittextExpirationDateYear.getText().charAt(0));
                    yearInput *= 10;
                    yearInput += Character.getNumericValue(edittextExpirationDateYear.getText().charAt(1));
                    yearInput += 2000;
                }

                if (isExpirationDateIsValide(monthInput, yearInput)) {
                    edittextExpirationDateMonth.setTextColor(Color.BLACK);
                    edittextExpirationDateYear.setTextColor(Color.BLACK);
                    edittextExpirationDateYear.setError(null);
                }
                else {
                    edittextExpirationDateMonth.setTextColor(Color.RED);
                    edittextExpirationDateYear.setTextColor(Color.RED);
                    edittextExpirationDateYear.setError("La date d'expiration ne peut pas être antérieur à la date actuelle");
                }
            }
        }
        // Mettre un 0 au besoin
    }

    private boolean isExpirationDateIsValide(int monthInput, int yearInput) {
        int monthCalendar;
        int yearCalendar;

        Calendar calendar = Calendar.getInstance();
        monthCalendar = calendar.get(Calendar.MONTH) + 1;
        yearCalendar = calendar.get(Calendar.YEAR);

        if (yearInput > yearCalendar && monthInput > 0 && monthInput <= 12)
            return true;
        else if (yearInput == yearCalendar && monthInput >= monthCalendar && monthInput > 0 && monthInput <= 12)
            return true;
        return false;
    }

    private void configureEdittextCardNumber() {

        edittextCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    cardNumberStr = edittextCardNumber.getText().toString();
                    if (edittextCardNumber.getText().length() != 0 && isCardNumberIsValide() == 0) {
                        Log.v("verifiyCardNumber", "La carte est valide");
                        edittextCardNumber.setTextColor(Color.BLACK);
                        edittextCardNumber.setError(null);
                    }
                    else {
                        Log.v("verifiyCardNumber", "La carte n'est pas valide");
                        edittextCardNumber.setTextColor(Color.RED);
                        edittextCardNumber.setError("Le numéro de carte n'est pas valide");
                    }
                }
            }
        });
    }

    private int isCardNumberIsValide() {
        // *2 every 2 number
        int[] resultTab = new int[15];
        for (int i = 0; i < cardNumberStr.length() - 1; i++) {
            if (i % 2 == 0) {
                int tmp = Character.getNumericValue(cardNumberStr.charAt(i));
                tmp *= 2;
                if (tmp > 9) {
                    tmp -= 9;
                }
                resultTab[i] = tmp;
            } else {
                resultTab[i] = Character.getNumericValue(cardNumberStr.charAt(i));
            }
        }

        // Add all the number beetween them
        int totalSum = 0;
        for (int aResultTab : resultTab) {
            totalSum += aResultTab;
        }

        // % 10 and 10 - result
        totalSum = totalSum % 10;
        totalSum = 10 - totalSum;
        return totalSum - Character.getNumericValue(cardNumberStr.charAt(cardNumberStr.length() - 1));

    }

    private void buttonBinding() {
        edittextCardNumber = (EditText)findViewById(R.id.edittextCardNumber);
        edittextExpirationDateMonth = (EditText)findViewById(R.id.edittextExpirationDateMonth);
        edittextExpirationDateYear = (EditText)findViewById(R.id.edittextExpirationDateYear);
        edittextCryptogram = (EditText)findViewById(R.id.edittextCryptogram);
        edittextOwnerName = (EditText)findViewById(R.id.edittextOwnerName);
        edittextDisplayName = (EditText)findViewById(R.id.edittextDisplayName);
        saveCard = (Button)findViewById(R.id.buttonSaveCard);
        scanCard = (ImageButton)findViewById(R.id.buttonScanCard);
    }

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);

        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String card_number = "", card_number_hide = "", card_number2 = "", cvv = "", expiryYear = "0";
        int expiryMonth = 0;

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                card_number = scanResult.getFormattedCardNumber();
                card_number_hide = scanResult.getRedactedCardNumber();
                card_number2 = scanResult.cardNumber;
                expiryMonth = scanResult.expiryMonth;
                expiryYear = ""+scanResult.expiryYear;
                cvv = scanResult.cvv;
                edittextCardNumber.setText(card_number2);
                if (expiryMonth >= 1 && expiryMonth <= 9)
                    edittextExpirationDateMonth.setText("0"+expiryMonth);
                else
                    edittextExpirationDateMonth.setText(expiryMonth);
                expiryYear = expiryYear.replace("20", "");
                edittextExpirationDateYear.setText(expiryYear);
                edittextCryptogram.setText(cvv);
                edittextOwnerName.requestFocus();
            }
            else {
                // Scan was canceled
            }

            /* DEBUG POUR MATHIEU
             *
              * A supprimer avant la mise en prod :D
              *
              * */
           /* Log.d("TEST", card_number); //1234 1234 1234 1234
            Log.d("TEST", card_number_hide);// •••• •••• •••• 1234
            Log.d("TEST", card_number2); //1234123412341234
            Log.d("TEST", ""+expiryMonth); // 4
            Log.d("TEST", ""+expiryYear); // 2014
            Log.d("TEST", cvv); // 123*/
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
