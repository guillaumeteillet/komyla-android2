package eip.com.lizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


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

        if (isLogged)
        {
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
                        Intent intent = new Intent(getBaseContext(), AddEditPaymentMethodActivity.class);
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
