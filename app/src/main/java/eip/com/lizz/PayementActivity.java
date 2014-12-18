package eip.com.lizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class PayementActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_lizz);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("unique_code") != null) {
                String unique_code = bundle.getString("unique_code");

                Log.d("UniqueCode", ">>" + unique_code);

                SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                boolean isLogged = sharedpreferences.getBoolean("eip.com.lizz.isLogged", false);

                if (isLogged) {


                } else {
                    Intent loggedUser = new Intent(getBaseContext(), HomeActivity.class);
                    loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loggedUser);
                }

            } else {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.no_unique_code), Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.no_unique_code), Toast.LENGTH_LONG).show();
            finish();
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
