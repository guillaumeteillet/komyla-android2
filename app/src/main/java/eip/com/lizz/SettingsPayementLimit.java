package eip.com.lizz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;


public class SettingsPayementLimit extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_payement_limit);

        final EditText payementLimit = (EditText) findViewById(R.id.payement_limit);

        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        String payementLimitSave = sharedpreferences.getString("eip.com.lizz.payementLimit", "0");

        payementLimit.setText(payementLimitSave);

        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                final String pinCode = sharedpreferences.getString("eip.com.lizz.codepinlizz", "");

                final EditText input = new EditText(getBaseContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                input.setTextColor(Color.BLACK);
                final AlertDialog.Builder alert = AlertBox.alertInputOk(SettingsPayementLimit.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_hint_pin), input);
                alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (input.getText().toString().equals(pinCode))
                        {
                            SaveParams.saveParamsString(SettingsPayementLimit.this, "eip.com.lizz.payementLimit",  payementLimit.getText().toString());
                        }
                        else
                        {
                            SaveParams.displayError(9, SettingsPayementLimit.this, null, null, false);
                        }
                    }
                });
                alert.show();

            }
        });

        final Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               finish();
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
