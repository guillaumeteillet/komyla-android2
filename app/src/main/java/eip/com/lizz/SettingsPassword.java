package eip.com.lizz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SettingsPassword extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_password);

        final EditText mdp = (EditText) findViewById(R.id.newpassword);
        final EditText mdpConfirm = (EditText) findViewById(R.id.confirmpassword);

        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (mdp.getText().toString().equals(mdpConfirm.getText().toString()))
                {
                    if (mdp.getText().toString().length() >= 6)
                    {
                        final EditText input = new EditText(getBaseContext());
                        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        input.setTextColor(Color.BLACK);
                        final AlertDialog.Builder alert = AlertBox.alertInputOk(SettingsPassword.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_hint_pwd_change), input);
                        alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String old_pwd = input.getText().toString();
                                String new_pwd = mdp.getText().toString();
                                final ProgressDialog progress;
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                progress = ProgressDialog.show(SettingsPassword.this, getResources().getString(R.string.pleasewait), getResources().getString(R.string.pleasewaitsave), true);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run()
                                            {
                                                // ENVOI des old_pwd et de new_pwd à l'API

                                                //Gestion des erreurs : Internet déco, wrong password...

                                                // Lorsque tout c'est bien passé.
                                                progress.dismiss();
                                                Toast.makeText(getBaseContext(), getResources().getString(R.string.pwdsave), Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        });
                                    }
                                }).start();
                            }

                        });
                        alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
                        alert.show();
                    }
                    else
                        AlertBox.alertOk(SettingsPassword.this, getResources().getString(R.string.error), getResources().getString(R.string.passwordTooShort));
                }
                else
                    AlertBox.alertOk(SettingsPassword.this, getResources().getString(R.string.error), getResources().getString(R.string.wrongPasswordConfirm));

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
        return MenuLizz.settings_menu(item, getBaseContext(), SettingsPassword.this);
    }
}
