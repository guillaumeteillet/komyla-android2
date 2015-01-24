package eip.com.lizz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class SettingsActivity extends ActionBarActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);// Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        String[] values = new String[] { "Limite de paiement",
                "Code PIN Lizz",
                "Contrôle Parental",
                "Scanner",
                "Votre Relevé d'Identité Bancaire",
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int itemPosition     = position;
                String  itemValue    = (String) listView.getItemAtPosition(position);

                Intent loggedUser;


                switch (itemPosition)
                {
                    case 0:
                        loggedUser = new Intent(getBaseContext(), SettingsPayementLimit.class);
                        startActivity(loggedUser);
                        break;
                    case 1:
                        loggedUser = new Intent(getBaseContext(), SettingsCodePIN.class);
                        startActivity(loggedUser);
                        break;
                    case 2:
                        loggedUser = new Intent(getBaseContext(), SettingsParentalControl.class);
                        startActivity(loggedUser);
                        break;
                    case 3:
                        loggedUser = new Intent(getBaseContext(), SettingsScanner.class);
                        startActivity(loggedUser);
                        break;
                    case 4:
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
}
