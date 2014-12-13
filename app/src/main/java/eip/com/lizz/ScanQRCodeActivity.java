package eip.com.lizz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class ScanQRCodeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_lizz);

            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt(getResources().getString(R.string.dialog_scan_qr_code));
            integrator.setResultDisplayDuration(0);
            integrator.setScanningRectangle(700,700);
            integrator.setCameraId(0);
            integrator.initiateScan();

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {
                if (contents.length() >= 20) {
                    String urlLizzOrNot = contents.substring(0, 20);
                    if (urlLizzOrNot.equals(getResources().getString(R.string.urllizzcode))) {
                        Intent loggedUser = new Intent(getBaseContext(), PayementActivity.class);
                        startActivity(loggedUser);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.errorQRCodeNotLizz), Toast.LENGTH_LONG).show();
                        Intent loggedUser = new Intent(getBaseContext(), ScanQRCodeActivity.class);
                        startActivity(loggedUser);
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.errorQRCodeNotLizz), Toast.LENGTH_LONG).show();
                    Intent loggedUser = new Intent(getBaseContext(), ScanQRCodeActivity.class);
                    startActivity(loggedUser);
                    finish();
                }
            } else {
               finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_lizz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_params) {
            Intent loggedUser = new Intent(getBaseContext(), SettingsActivity.class);
            loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loggedUser);
            return true;
        }
        if (id == R.id.action_settings) {
            SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
            sharedpreferences.edit().putBoolean("eip.com.lizz.isLogged", false).apply();
            Intent loggedUser = new Intent(getBaseContext(), HomeActivity.class);
            loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loggedUser);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
