package eip.com.lizz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import eip.com.lizz.Utils.UAlertBox;


public class PayementPTPActivity extends ActionBarActivity {

    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payementptp);

        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        boolean isLogged = sharedpreferences.getBoolean("eip.com.lizz.isLogged", false);

        if (isLogged) {

            Spinner payement = (Spinner) findViewById(R.id.payementMethod);
            payementMethod(true, payement);

            Button select = (Button) findViewById(R.id.select);
            select.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
                }
            });

            Button next = (Button) findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    EditText contact_input = (EditText) findViewById(R.id.contact);
                    EditText somme_input = (EditText) findViewById(R.id.somme);
                    String contact_a_check = contact_input.getText().toString();
                    String somme = somme_input.getText().toString();
                    String id_payement = payementMethod(false, null);
                    boolean isEmail = LoginActivity.isEmailValid(contact_a_check);
                    boolean isPhone = isPhoneValid(contact_a_check);
                    if (contact_a_check.isEmpty())
                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_contact_empty));
                    else if (somme.isEmpty())
                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_somme_empty));
                    else if (!isEmail && !isPhone)
                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_contact_ptp));
                    else if (id_payement.isEmpty())
                        UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_id_payement));
                    else
                    {
                        Intent paiement = new Intent(getBaseContext(), PayementPTPConfirmActivity.class);
                        paiement.putExtra("contact", contact_a_check);
                        paiement.putExtra("somme", somme);
                        paiement.putExtra("isEmail", isEmail);
                        paiement.putExtra("isPhone", isPhone);
                        paiement.putExtra("idPayment", id_payement);
                        startActivity(paiement);
                    }
                }
            });

            }

            else

            {
                Intent loggedUser = new Intent(getBaseContext(), HomeActivity.class);
                loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loggedUser);
            }
        }

    private String payementMethod(Boolean isDisplay, Spinner spinner) {

        if (isDisplay)
        {
            List<String> list = new ArrayList<String>();
            list.add("Carte Bleu Visa");
            list.add("Paypal Account pdg@lizz.fr");
            list.add("Compte chèque restaurant");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            return "";
        }
        else
        {
            return "ID231KE2";
        }
    }

    private boolean isPhoneValid(String contact_a_check) {

        if (!contact_a_check.matches("[0-9]+"))
        {
            return false;
        }
        if (contact_a_check.length() >= 10 && contact_a_check.length() <= 12)
        {
            if(contact_a_check.substring(0, 2).equals("33") || contact_a_check.substring(0, 2).equals("06") || contact_a_check.substring(0, 2).equals("07"))
                return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c =  getContentResolver().query(contactData, null, null, null, null);
            int phoneIdx;
            if (c.moveToFirst()) {
                name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                Uri result = data.getData();
                String id = result.getLastPathSegment();

                boolean phone_multiple = false;
                boolean email_multiple = false;
                final String[] forbidden = {" ",",","#","$","%","&","+","(",")","*","\"","'",":",";","!","?","/","~","`","|","•","√","π","÷","×","¶","∆","}","{","=","°","^","¥","€","¢","£","\\","©","®","™","℅","[","]",">","<"};
                final String[] allowed = {"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""};
                Cursor Cphone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
                Cursor emailCur = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                int nb_phone = Cphone.getCount();
                int nb_email = emailCur.getCount();
                final CharSequence[] contact = new CharSequence[Cphone.getCount() + emailCur.getCount()];
                int i=0;
                while (emailCur.moveToNext()) {
                    contact[i++] = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    email_multiple = true;
                }
                emailCur.close();

                while(Cphone.moveToNext()) {
                    phoneIdx = Cphone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    contact[i++] = Cphone.getString(phoneIdx);
                    phone_multiple = true;
                }
                Cphone.close();

                if ((nb_email + nb_phone) == 1)
                {
                    String contact_string = (String) contact[0];
                    EditText contact_input = (EditText) findViewById(R.id.contact);
                    for (int index =0; index < forbidden.length; index++){
                        contact_string = contact_string.replace(forbidden[index], allowed[index]);
                    }
                    contact_input.setText(contact_string);
                }
                else if ((nb_email + nb_phone) > 1)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    if (phone_multiple == true && email_multiple == true)
                        builder.setTitle(name+getResources().getString(R.string.label_phone_or_email));
                    else if (phone_multiple)
                        builder.setTitle(name+getResources().getString(R.string.label_phone));
                    else if (email_multiple)
                        builder.setTitle(name+getResources().getString(R.string.label_email));
                    builder.setItems(contact, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            String contact_string = (String) contact[item];
                            EditText contact_input = (EditText) findViewById(R.id.contact);
                            for (int index =0; index < forbidden.length; index++){
                                contact_string = contact_string.replace(forbidden[index], allowed[index]);
                            }
                            contact_input.setText(contact_string);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setOwnerActivity(this);
                    alert.show();
                }
                else
                {
                    EditText contact_input = (EditText) findViewById(R.id.contact);
                    contact_input.setText("");
                    UAlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_contact));
                }

            }
        }
    }

            @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.main_menu(item, getBaseContext(), PayementPTPActivity.this);
    }
}
