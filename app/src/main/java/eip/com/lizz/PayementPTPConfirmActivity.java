package eip.com.lizz;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class PayementPTPConfirmActivity extends ActionBarActivity {

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

                if (api)
                {
                    name_label.setText("MATHIEU ROBLIN API"); // A remplacer par le retour API.
                }
                else
                {
                    ContentResolver cr = getContentResolver();
                    String contactName = "", id = "";
                    if (isPhone)
                    {
                        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contact));
                        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};
                        Cursor cursor =
                                cr.query(
                                        uri,
                                        projection,
                                        null,
                                        null,
                                        null);

                        if(cursor!=null) {
                            if(cursor.moveToFirst()) {
                                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                                id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                            }
                            cursor.close();
                        }
                    }
                    else if (isEmail)
                    {
                        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Email.CONTACT_ID};
                        Cursor emailCur = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                projection,
                                ContactsContract.CommonDataKinds.Email.DATA
                                        + " = '" + contact+"'", null, null);
                        while (emailCur.moveToNext()) {
                            contactName = emailCur.getString(emailCur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                           id = emailCur.getString(emailCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.CONTACT_ID));
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
                    if (!id.equals(""))
                        profil_picture.setImageURI(Uri.parse(getPhotoUri(id).toString()));
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
        return MenuLizz.main_menu(item, getBaseContext(), PayementPTPConfirmActivity.this);
    }
}