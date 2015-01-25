package eip.com.lizz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;


public class HomeLizzActivity extends ActionBarActivity {

    boolean scannerStatus;
    boolean isLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_lizz);

       SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        isLogged = sharedpreferences.getBoolean("eip.com.lizz.isLogged", false);
        sharedpreferences.edit().putBoolean("eip.com.lizz.flash", false).apply(); // flash off
        String firstname, surname, email, phone;

        firstname = sharedpreferences.getString("eip.com.lizz.firstname", "");
        surname = sharedpreferences.getString("eip.com.lizz.surname", "");
        email = sharedpreferences.getString("eip.com.lizz.email", "");
        phone = sharedpreferences.getString("eip.com.lizz.phone", "");

        Log.d("DEBUG LOL", ">>>> "+firstname+ " -- "+ surname + "-- "+ email +"---"+phone+"--");

        if (isLogged)
        {
            checkSIMNumber();

            Button payer = (Button) findViewById(R.id.payer);
            payer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                    scannerStatus = sharedpreferences.getBoolean("eip.com.lizz.scannerstatus", true);
                    boolean apn = CameraPreview.checkCameraHardware(getBaseContext());
                    if (scannerStatus && apn)
                    {
                        Intent intent = new Intent(getBaseContext(), ScanQRCodeActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(getBaseContext(), PayementWithUniqueCodeActivity.class);
                        startActivity(intent);
                    }
                }
            });

            Button AddPaymentMethod = (Button) findViewById(R.id.add);
            AddPaymentMethod.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), PaymentMethodsActivity.class);
                        startActivity(intent);
                }
            });

            Button ViewTickets = (Button)findViewById(R.id.tickets);
            ViewTickets.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getBaseContext(), PaymentHistoryActivity.class);
                    startActivity(intent);
                }
            });
        }
        else
        {
            Intent loggedUser = new Intent(getBaseContext(), HomeActivity.class);
            loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loggedUser);
        }
    }

    private void checkSIMNumber() {

        final TelephonyManager tMgr = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (!tMgr.getLine1Number().equals("") && tMgr.getLine1Number() != null)
        {
            String phone;
            boolean isNew = true;
            boolean loop = true;
            int i = 0;
            final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
            phone = sharedpreferences.getString("eip.com.lizz.phone", "");
            Log.d("PHONE", phone);
            String[] phones = phone.split(";");
            Log.d("NB Case tableau",  ">>"+phones.length+"---"+loop+"---"+tMgr.getLine1Number()+"-0:"+phones[0]);
            while((i <= (phones.length - 1)) && loop == true) {
                Log.d("WHILE",  ">>"+i+"---"+phones.length);
                if (tMgr.getLine1Number().equals(phones[i])) {
                    isNew = false;
                    loop = false;
                }
                i++;
            }

            if (isNew)
            {
                AlertDialog.Builder alert;
                alert = AlertBox.alert(HomeLizzActivity.this, getResources().getString(R.string.dialog_new_phone_number), getResources().getString(R.string.dialog_new_phone_number_txt1) + tMgr.getLine1Number() + getResources().getString(R.string.dialog_new_phone_number_txt2));
                alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String phone_before;
                        phone_before = sharedpreferences.getString("eip.com.lizz.phone", "");
                        sharedpreferences.edit().putString("eip.com.lizz.phone", phone_before+tMgr.getLine1Number()+";").apply();
                    }
                });
                alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
                alert.show();
            }
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

    public void onResume()
    {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putBoolean("eip.com.lizz.flash", false).apply();
    }
}
