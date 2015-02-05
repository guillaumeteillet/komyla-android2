package eip.com.lizz;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;


public class PayementPTPConfirm extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payement_ptpconfirm);

        boolean api = false;

        // On check si le contact est associé à un compte lizz ou pas. Si oui, on set le nom du contact de l'API sur le label.

        TextView name_label = (TextView) findViewById(R.id.name_destinataire);
        TextView somme_label = (TextView) findViewById(R.id.somme);
        TextView contact_label = (TextView) findViewById(R.id.contactLabel);
        TextView no_account_label = (TextView) findViewById(R.id.no_account);
        ImageView profil_picture = (ImageView) findViewById(R.id.profil_picture);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("contact") != null) {

                boolean isEmail = bundle.getBoolean("isEmail");
                boolean isPhone = bundle.getBoolean("isPhone");
                String contact = bundle.getString("contact");
                contact_label.setText(contact);
                profil_picture.setImageURI(Uri.parse(bundle.getString("picture_URI")));
                if(profil_picture.getDrawable() == null)
                    profil_picture.setImageResource(R.drawable.ic_launcher);

                if (api)
                {
                    name_label.setText("MATHIEU ROBLIN API"); // A remplacer par le retour API.
                }
                else
                {
                    ContentResolver cr = getContentResolver();
                    String contactName = "";
                    if (isPhone)
                    {
                        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contact));
                        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

                        if(cursor.moveToFirst()) {
                            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                        }
                        cursor.close();
                    }
                    else if (isEmail)
                    {
                        Cursor emailCur = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.DATA
                                        + " = '" + contact+"'", null, null);
                        while (emailCur.moveToNext()) {
                            contactName = emailCur.getString(emailCur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                        }
                        emailCur.close();
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
                }
            }
            if (bundle.getString("somme") != null) {
                String somme = bundle.getString("somme");
                somme_label.setText(somme+" €");
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
        return MenuLizz.main_menu(item, getBaseContext(), PayementPTPConfirm.this);
    }
}