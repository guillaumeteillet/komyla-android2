package eip.com.lizz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import eip.com.lizz.Protocols.PED;
import eip.com.lizz.QueriesAPI.SendSMSToAPI;
import eip.com.lizz.QueriesAPI.SendTransactionPTPToAPI;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Utils.UDownload;
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
            if (bundle.getBoolean("isForced"))
            {
                checkSimCARD(bundle);
            }
        }


        Button confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 checkSimCARD(bundle);
            }
        });
    }

    private void checkSimCARD(Bundle bundle) {
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

    public void popUpPIN(final Bundle bundle)
    {
        final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);

        final String id_payement_method = bundle.getString("idPayment");
        final String PIN = sharedpreferences.getString("eip.com.lizz.codepinlizz", "");
        final String email_sender = sharedpreferences.getString("eip.com.lizz.email", "");
        final String receiver = bundle.getString("contact");
        final String id_user = sharedpreferences.getString("eip.com.lizz.id_user", "");
        final String amount = bundle.getString("somme");
        try {
            tryToSecureMsg(id_payement_method, PIN, email_sender, receiver, id_user, amount, bundle, sharedpreferences);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | PackageManager.NameNotFoundException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | NoSuchProviderException | IllegalBlockSizeException e) {
            AlertDialog.Builder alert;
            alert = UAlertBox.alert(PayementPTPConfirmActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_rsa_key));
            final ProgressDialog progress = new ProgressDialog(PayementPTPConfirmActivity.this);
            alert.setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    new Thread(new Runnable() {
                        @Override
                        public void run()
                        {
                           runOnUiThread(new Runnable() {
                                public void run() {

                                    progress.setTitle(getResources().getString(R.string.dialog_download));
                                    progress.setMessage(getResources().getString(R.string.dialog_download_rsa_key));
                                    progress.setCancelable(false);
                                    progress.show();
                                }
                            });
                            String isOk;
                            isOk = UDownload.downloadFile("http://test-ta-key.lizz.fr/ped.der", "ped.pub", getBaseContext());
                            if (isOk.equals("ok"))
                            {
                                progress.dismiss();
                                finish();
                                Intent paiement = new Intent(getBaseContext(), PayementPTPConfirmActivity.class);
                                paiement.putExtra("contact", receiver);
                                paiement.putExtra("somme", amount);
                                paiement.putExtra("isEmail", bundle.getBoolean("isEmail"));
                                paiement.putExtra("isPhone", bundle.getBoolean("isPhone"));
                                paiement.putExtra("idPayment", id_payement_method);
                                paiement.putExtra("isForced", true);
                                startActivity(paiement);
                            }
                        }
                    }).start();
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    UAlertBox.alertOk(PayementPTPConfirmActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_rsa_key_cancel));
                }
            });
            alert.show();
        }
    }

    public void tryToSecureMsg(String id_payement_method, final String PIN, String email_sender, String receiver, String id_user, String amount, final Bundle bundle, final SharedPreferences sharedpreferences) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, PackageManager.NameNotFoundException, NoSuchPaddingException, BadPaddingException, InvalidKeySpecException, IllegalBlockSizeException {
        final String token = PED.cryptped(id_payement_method, PIN, email_sender, receiver, id_user, amount, getBaseContext(), PayementPTPConfirmActivity.this);
        final Intent paiement = new Intent(getBaseContext(), PayementPTPFinishActivity.class);
        paiement.putExtra("somme", amount);
        paiement.putExtra("contact", bundle.getString("contact"));
        paiement.putExtra("isEmail", bundle.getBoolean("isEmail"));
        paiement.putExtra("isPhone", bundle.getBoolean("isPhone"));
        paiement.putExtra("isInternet", bundle.getBoolean("isInternet"));
        final EditText input = new EditText(getBaseContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setTextColor(Color.BLACK);
        final AlertDialog.Builder alert = UAlertBox.alertInputOk(PayementPTPConfirmActivity.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_pin), input);
        alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                confirmTransaction(input, token, paiement, PIN, sharedpreferences, bundle.getBoolean("isInternet"));
            }

        });
        alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
        alert.show();
    }

    public void confirmTransaction(EditText input, String token, Intent paiement, String PIN, SharedPreferences sharedpreferences, Boolean isInternet)
    {
        if (input.getText().toString().equals(PIN))
        {
            sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", 0).apply();
            if (getResources().getString(R.string.debugOrProd).equals("PROD"))
            {
                if (isInternet)
                {
                    Log.d("DEBUG MODE", "PAIEMENT INTERNET");
                    sendTransactionToAPI(token, sharedpreferences, paiement);
                }
                else
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
            }
            else
            {
                if (isInternet)
                {
                    Log.d("DEBUG MODE", "PAIEMENT PED INTERNET >>>>" + token);
                    sendTransactionToAPI(token, sharedpreferences, paiement);
                }
                else {
                    Log.d("DEBUG MODE", "PAIEMENT PED SMS >>>>" + token);
                    startActivity(paiement);
                    finish();
                }
            }
        }
        else
        {
            USaveParams.tentativeCheck(PayementPTPConfirmActivity.this, sharedpreferences);
        }
    }

    private void sendTransactionToAPI(final String token, final SharedPreferences sharedpreferences, final Intent paiement) {
        final ProgressDialog progress = ProgressDialog.show(PayementPTPConfirmActivity.this, getResources().getString(R.string.pleasewait), getResources().getString(R.string.pleasewaitTransaction), true);
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        SendTransactionPTPToAPI mAuthTask = new SendTransactionPTPToAPI(sharedpreferences.getString("eip.com.lizz._csrf", ""), getBaseContext(), token);
                        mAuthTask.setOnTaskFinishedEvent(new SendTransactionPTPToAPI.OnTaskExecutionFinished() {

                            @Override
                            public void OnTaskFihishedEvent(HttpResponse httpResponse) {
                                dataAPI(progress, httpResponse, paiement);
                            }
                        });
                        mAuthTask.execute();
                    }
                });
            }
        }).start();
    }

    private void dataAPI(ProgressDialog progress, HttpResponse httpResponse, Intent paiement)
    {
        InputStream inputStream = null;
        try {
            progress.dismiss();
            inputStream = httpResponse.getEntity().getContent();
            String jString =  UApi.convertStreamToString(inputStream);
            JSONObject jObj = new JSONObject(jString);

            int responseCode = httpResponse.getStatusLine().getStatusCode();

            Log.d("RETOUR API", ">>>"+responseCode+"---");
            startActivity(paiement);
            finish();
        } catch (IOException e) {
            progress.dismiss();
            e.printStackTrace();
        } catch (Exception e) {
            progress.dismiss();
            e.printStackTrace();
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