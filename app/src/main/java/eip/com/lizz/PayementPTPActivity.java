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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class PayementPTPActivity extends ActionBarActivity {

    String name = "", picture_URI = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payementptp);

        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        boolean isLogged = sharedpreferences.getBoolean("eip.com.lizz.isLogged", false);

        if (isLogged) {

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
                    boolean isEmail = LoginActivity.isEmailValid(contact_a_check);
                    boolean isPhone = isPhoneValid(contact_a_check);
                    if (contact_a_check.isEmpty())
                        AlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_contact_empty));
                    else if (somme.isEmpty())
                        AlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_somme_empty));
                    else if (!isEmail && !isPhone)
                        AlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_contact_ptp));
                    else
                    {
                        Intent paiement = new Intent(getBaseContext(), PayementPTPConfirm.class);
                        paiement.putExtra("contact", contact_a_check);
                        paiement.putExtra("somme", somme);
                        paiement.putExtra("isEmail", isEmail);
                        paiement.putExtra("isPhone", isPhone);
                        paiement.putExtra("picture_URI", picture_URI);
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

    private boolean isPhoneValid(String contact_a_check) {

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
                picture_URI = getPhotoUri(id).toString();

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
                    AlertBox.alertOk(PayementPTPActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_contact));
                }

            }
        }
    }

    public Uri getPhotoUri(String id) {
        try {
            Cursor cur = getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + id + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(id));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
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
