package eip.com.lizz;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import eip.com.lizz.Protocols.PED;
import eip.com.lizz.QueriesAPI.SendSMSToAPI;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UNetwork;
import eip.com.lizz.Utils.UPhoneBook;
import eip.com.lizz.Utils.USaveParams;


public class PayementPTPConfirmActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payement_ptpconfirm);

        boolean api = false;
        final Bundle bundle = getIntent().getExtras();
        // On check si le contact est associé à un compte lizz ou pas. Si oui, on set le nom du contact de l'API sur le label.

        TextView name_label = (TextView) findViewById(R.id.name_destinataire);
        TextView somme_label = (TextView) findViewById(R.id.somme);
        TextView contact_label = (TextView) findViewById(R.id.contactLabel);
        TextView no_account_label = (TextView) findViewById(R.id.no_account);
        ImageView profil_picture = (ImageView) findViewById(R.id.profil_picture);

        if (bundle != null) {
            if (bundle.getString("contact") != null) {

                boolean isEmail = bundle.getBoolean("isEmail");
                boolean isPhone = bundle.getBoolean("isPhone");
                String contact = bundle.getString("contact");
                contact_label.setText(contact);

                if (api)
                {
                    name_label.setText("MATHIEU ROBLIN API"); // A remplacer par le retour API.
                }
                else
                {
                    String contactName = "", uri = "";
                    if (isPhone)
                    {
                        String[] array = UPhoneBook.infosByPhone(getContentResolver(), contact);
                        contactName = array[0];
                        uri = array[1];
                    }
                    else if (isEmail)
                    {
                        String[] array = UPhoneBook.infosByEmail(getContentResolver(), contact);
                        contactName = array[0];
                        uri = array[1];
                    }
                    if (contactName.equals(""))
                    {
                        name_label.setText(contact);
                        contact_label.setVisibility(View.GONE);
                        if (isEmail)
                            no_account_label.setText(contact+" "+getResources().getString(R.string.label_no_account_email));
                        else if (isPhone)
                            no_account_label.setText(contact+" "+getResources().getString(R.string.label_no_account_sms));
                    }
                    else
                    {
                        name_label.setText(contactName);
                        if (isEmail)
                            no_account_label.setText(contactName+" ("+contact+") "+getResources().getString(R.string.label_no_account_email));
                        else if (isPhone)
                            no_account_label.setText(contactName+" ("+contact+") "+getResources().getString(R.string.label_no_account_sms));
                    }
                    if (!uri.equals(""))
                        profil_picture.setImageURI(Uri.parse(uri));
                    else
                        profil_picture.setImageResource(R.drawable.ic_launcher);
                    if(profil_picture.getDrawable() == null)
                        profil_picture.setImageResource(R.drawable.ic_launcher);
                }
            }
            if (bundle.getString("somme") != null) {
                String somme = bundle.getString("somme");
                somme_label.setText(somme+" €");
            }
        }


        Button confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                TelephonyManager tMgr = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                if ((tMgr.getLine1Number() == null || tMgr.getLine1Number().equals("")))
                    UAlertBox.alertOk(PayementPTPConfirmActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.error_sim_card));
                else if (!UNetwork.isMobileAvailable(getBaseContext()))
                    UAlertBox.alertOk(PayementPTPConfirmActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.error_network_phone));
                else
                {
                    popUpPIN(bundle);
                }
            }
        });
    }

    public void popUpPIN(Bundle bundle)
    {
        final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);

        String id_payement_method = bundle.getString("idPayment");
        final String PIN = sharedpreferences.getString("eip.com.lizz.codepinlizz", "");
        String email_sender = sharedpreferences.getString("eip.com.lizz.email", "");
        String receiver = bundle.getString("contact");
        String id_user = sharedpreferences.getString("eip.com.lizz.id_user", "");
        String amount = bundle.getString("somme");
        final String token = PED.cryptped(id_payement_method, PIN, email_sender, receiver, id_user, amount, getBaseContext());
        final Intent paiement = new Intent(getBaseContext(), PayementPTPFinishActivity.class);
        paiement.putExtra("somme", amount);
        paiement.putExtra("contact", bundle.getString("contact"));
        paiement.putExtra("isEmail", bundle.getBoolean("isEmail"));
        paiement.putExtra("isPhone", bundle.getBoolean("isPhone"));
        final EditText input = new EditText(getBaseContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setTextColor(Color.BLACK);
        final AlertDialog.Builder alert = UAlertBox.alertInputOk(PayementPTPConfirmActivity.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_pin), input);
        alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                confirmTransaction(input, token, paiement, PIN, sharedpreferences);
            }

        });
        alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
        alert.show();
    }

    public void confirmTransaction(EditText input, String token, Intent paiement, String PIN, SharedPreferences sharedpreferences)
    {
        if (input.getText().toString().equals(PIN))
        {
            sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", 0).apply();
            if (getResources().getString(R.string.debugOrProd).equals("PROD"))
            {
                int err = SendSMSToAPI.send(token);
                if (err == 0)
                {
                    startActivity(paiement);
                    finish();
                }
                else
                {
                    UAlertBox.alertOk(PayementPTPConfirmActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_send_sms));
                }
            }
            else
            {
                Log.d("DEBUG MODE", "PAIEMENT PED SMS >>>>"+token);
                startActivity(paiement);
                finish();
            }
        }
        else
        {
            USaveParams.tentativeCheck(PayementPTPConfirmActivity.this, sharedpreferences);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.main_menu(item, getBaseContext(), PayementPTPConfirmActivity.this);
    }
}