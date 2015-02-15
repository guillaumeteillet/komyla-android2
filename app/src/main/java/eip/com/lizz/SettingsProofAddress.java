package eip.com.lizz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Settings;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import eip.com.lizz.Utils.UNetwork;
import eip.com.lizz.Utils.UPhoto;
import eip.com.lizz.Utils.UThread;


public class SettingsProofAddress extends ActionBarActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    private static int RESULT_LOAD_IMG = 2;
    String imgDecodableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_proof_address);

        final Button select = (Button) findViewById(R.id.select);
        final Button take = (Button) findViewById(R.id.take);

        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        String returnAPI = sharedpreferences.getString("eip.com.lizz.returnAPI", "0");

        if (UNetwork.checkInternetConnection(getApplicationContext()))
        {
            returnAPI = "1"; // RETOUR API
            sharedpreferences.edit().putString("eip.com.lizz.returnAPI",returnAPI).apply();
        }
        else
            AlertBox.alertOk(SettingsProofAddress.this, getResources().getString(R.string.dialog_title_no_internet), getResources().getString(R.string.dialog_no_internet));

        TextView statut = (TextView) findViewById(R.id.statut);
        if (returnAPI.equals("0")) // Pas encore recu
        {
            statut.setText(getResources().getString(R.string.label_proof_not_receive));
            statut.setTextColor(Color.RED);
        }
        else if (returnAPI.equals("1")) // Wait
        {
            statut.setText(getResources().getString(R.string.label_proof_wait));
            statut.setTextColor(Color.parseColor("#FF6633"));
        }
        else if (returnAPI.equals("2")) // OK
        {
            statut.setText(getResources().getString(R.string.label_proof_ok));
            statut.setTextColor(Color.parseColor("#336600"));
            take.setVisibility(View.GONE);
            select.setVisibility(View.GONE);
        }

        take.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UPhoto.launchAPN(SettingsProofAddress.this, REQUEST_TAKE_PHOTO, "proof_address.jpg", "/lizzTMP/");
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

               Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            UThread.send(SettingsProofAddress.this, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/lizzTMP/proof_address.jpg", getResources().getString(R.string.pleasewaitproofaddress), getResources().getString(R.string.labelMessageProofAddress));
        }
       else if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                UThread.send(SettingsProofAddress.this, imgDecodableString, getResources().getString(R.string.pleasewaitproofaddress), getResources().getString(R.string.labelMessageProofAddress));
                cursor.close();

            } catch (Exception e) {
                AlertBox.alertOk(SettingsProofAddress.this, getResources().getString(R.string.error), getResources().getString(R.string.errordefault));
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.settings_menu(item, getBaseContext(), SettingsProofAddress.this);
    }
}
