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
                        errorQRCode();
                    }
                }
                else
                {
                    errorQRCode();
                }
            } else {
               finish();
            }
        }
    }

    private void errorQRCode()
    {
        Toast.makeText(getBaseContext(), getResources().getString(R.string.errorQRCodeNotLizz), Toast.LENGTH_LONG).show();
        Intent loggedUser = new Intent(getBaseContext(), ScanQRCodeActivity.class);
        startActivity(loggedUser);
        finish();
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
