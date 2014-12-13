package eip.com.lizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Session;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class HomeLizzActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_lizz);

       SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        boolean isLogged = sharedpreferences.getBoolean("eip.com.lizz.isLogged", false);

        if (isLogged)
        {

            Button payer = (Button) findViewById(R.id.payer);
            payer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), ScanQRCodeActivity.class);
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
      //  Toast.makeText(getBaseContext(), ">>>"+isLogged, Toast.LENGTH_LONG).show();

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