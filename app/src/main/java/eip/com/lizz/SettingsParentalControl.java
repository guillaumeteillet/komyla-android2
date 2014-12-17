package eip.com.lizz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.Settings;


public class SettingsParentalControl extends ActionBarActivity {

    EditText mdp;
    Switch parentalControl;
    ToggleButton parentalControlOld;
    Boolean parentalControlStatus;
    String mdpBDD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        parentalControlStatus = sharedpreferences.getBoolean("eip.com.lizz.parentalcontrolstatus", false);
        mdpBDD = sharedpreferences.getString("eip.com.lizz.parentalcontrolpassword", "");

        if (Build.VERSION.SDK_INT >= 14) {
            setContentView(R.layout.activity_settings_parental_control);
            parentalControl = (Switch) findViewById(R.id.onOrOff);
            parentalControl.setChecked(parentalControlStatus);
            displayOrNot(parentalControlStatus);
            parentalControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    if(isChecked){
                        displayOrNot(true);
                    }
                    else
                    {
                        displayOrNot(false);
                    }
                }
            });
        }
        else
        {
            setContentView(R.layout.activity_settings_parental_control_old);
            parentalControlOld = (ToggleButton) findViewById(R.id.onOrOff);
            parentalControlOld.setChecked(parentalControlStatus);
            displayOrNot(parentalControlStatus);
            parentalControlOld.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    if(isChecked){
                        displayOrNot(true);
                    }
                    else
                    {
                        displayOrNot(false);
                    }
                }
            });
        }
      //  codePinEditText.setText(codePinSave);

        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                final String passwordInBDD = sharedpreferences.getString("eip.com.lizz.parentalcontrolpassword", "");
                final Boolean statusInBDD = sharedpreferences.getBoolean("eip.com.lizz.parentalcontrolstatus", false);

                if (parentalControlStatus) {
                    mdp = (EditText) findViewById(R.id.mdp);

                    if (mdp.getText().toString().equals(""))
                        SaveParams.displayError(2, SettingsParentalControl.this, null, null, false);
                    else if (passwordInBDD.equals(mdp.getText().toString()))
                        finish();
                    else {

                        final EditText input = new EditText(SettingsParentalControl.this);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        final AlertDialog.Builder alert = AlertBox.alertInputOk(SettingsParentalControl.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_mdp_parental), input);
                        alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (input.getText().toString().equals(mdp.getText().toString())) {

                                    // On check si c'est une modif de mot de passe ou un ajout.
                                    if (passwordInBDD.equals("")) { // AJOUT
                                        SaveParams.saveParamsString(SettingsParentalControl.this, "eip.com.lizz.parentalcontrolpassword", mdp.getText().toString());
                                        SaveParams.saveParamsBoolean(SettingsParentalControl.this, "eip.com.lizz.parentalcontrolstatus", true);
                                    }
                                    else
                                    {
                                        final EditText input2 = new EditText(SettingsParentalControl.this);
                                        input2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        input2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                        final AlertDialog.Builder alert = AlertBox.alertInputOk(SettingsParentalControl.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_old_mdp_parental), input2);
                                        alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                if (input2.getText().toString().equals(passwordInBDD)) {
                                                    SaveParams.saveParamsString(SettingsParentalControl.this, "eip.com.lizz.parentalcontrolpassword", mdp.getText().toString());
                                                    SaveParams.saveParamsBoolean(SettingsParentalControl.this, "eip.com.lizz.parentalcontrolstatus", true);
                                                }
                                                else
                                                {
                                                    SaveParams.displayError(4, SettingsParentalControl.this, null, null, false);
                                                    mdp.setText("");
                                                    input.setText("");
                                                    input2.setText("");
                                                }
                                            }
                                        });
                                        alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
                                        alert.show();
                                    }

                                } else {
                                    SaveParams.displayError(3, SettingsParentalControl.this, null, null, false);
                                    mdp.setText("");
                                    input.setText("");
                                }
                            }
                        });
                        alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
                        alert.show();
                    }
                }
                else
                {
                   if (!passwordInBDD.equals(""))
                   {
                       final EditText input3 = new EditText(SettingsParentalControl.this);
                       input3.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                       input3.setTransformationMethod(PasswordTransformationMethod.getInstance());
                       final AlertDialog.Builder alert = AlertBox.alertInputOk(SettingsParentalControl.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_old_mdp_parental), input3);
                       alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int whichButton) {


                               if (input3.getText().toString().equals(passwordInBDD)) {
                                   SaveParams.saveParamsString(SettingsParentalControl.this, "eip.com.lizz.parentalcontrolpassword", "");
                                   SaveParams.saveParamsBoolean(SettingsParentalControl.this, "eip.com.lizz.parentalcontrolstatus", false);
                               }
                               else
                               {
                                   SaveParams.displayError(5, SettingsParentalControl.this, null, null, false);
                                   input3.setText("");
                               }
                           }
                       });
                       alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
                       alert.show();
                   }
                   else
                        finish();
                }
            }
        });

        final Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               finish();
            }
        });
    }

    public void displayOrNot(Boolean display)
    {
        TextView parentalControl = (TextView) findViewById(R.id.hint_mdp);
        mdp = (EditText) findViewById(R.id.mdp);

        if (display)
        {
            parentalControl.setVisibility(View.VISIBLE);
            mdp.setVisibility(View.VISIBLE);
            mdp.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mdp.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mdp.setText(mdpBDD);
            parentalControlStatus = true;
        }
        else
        {
            parentalControl.setVisibility(View.GONE);
            mdp.setVisibility(View.GONE);
            mdp.setText("");
            parentalControlStatus = false;
        }

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
