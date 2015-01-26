package eip.com.lizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.iban4j.IbanFormatException;
import org.iban4j.IbanUtil;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;

public class SettingsProfil  extends ActionBarActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings_profil);

        listView = (ListView) findViewById(R.id.list);

        String[] values = new String[] {
                "Vos Coordonnées",
                "Vos moyens de paiement",
                "Votre Relevé d'Identité Bancaire",
        };
        final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putString("eip.com.lizz.phoneTMP", "").apply();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int itemPosition = position;
                String itemValue = (String) listView.getItemAtPosition(position);

                Intent loggedUser;


                switch (itemPosition) {
                    case 0:
                        loggedUser = new Intent(getBaseContext(), SettingsCoordonnees.class);
                        startActivity(loggedUser);
                        break;
                    case 1:
                        loggedUser = new Intent(getBaseContext(), PaymentMethodsActivity.class);
                        startActivity(loggedUser);
                        break;
                    case 2:
                        loggedUser = new Intent(getBaseContext(), SettingsRIB.class);
                        startActivity(loggedUser);
                        break;
                }

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.settings_menu(item, getBaseContext());
    }

    public void onResume()
    {
        super.onResume();
        final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putString("eip.com.lizz.phoneTMP", "").apply();
    }
}
