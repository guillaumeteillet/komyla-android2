package eip.com.lizz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class SettingsCodePIN extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_code_pin);

        final EditText codePinEditText = (EditText) findViewById(R.id.code_pin);

        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        String codePinSave = sharedpreferences.getString("eip.com.lizz.codepinlizz", "");

        codePinEditText.setText(codePinSave);
        codePinEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        codePinEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (codePinEditText.getText().toString().equals(""))
                    SaveParams.displayError(6, SettingsCodePIN.this, null, null, false);
                else
                    SaveParams.checkParentalControlStatus(SettingsCodePIN.this, codePinEditText.getText().toString(), "eip.com.lizz.codepinlizz", true);
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
