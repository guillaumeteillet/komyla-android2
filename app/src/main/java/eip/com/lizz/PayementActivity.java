package eip.com.lizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;


public class PayementActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payement);

        final SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        boolean isLogged = sharedpreferences.getBoolean("eip.com.lizz.isLogged", false);

        if (isLogged) {

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.getString("jObjString") != null) {
                    String jObjString = bundle.getString("jObjString");
                    try {
                        dataAPI(jObjString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.no_unique_code), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            else {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.no_unique_code), Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Intent loggedUser = new Intent(getBaseContext(), HomeActivity.class);
            loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loggedUser);
        }
    }

    void dataAPI(String jObjString) throws JSONException {
        JSONObject jObj = new JSONObject(jObjString);
        if(jObj.has(getResources().getString(R.string.api_checkout_picture)))
        {
            new LoadImage().execute(jObj.getString(getResources().getString(R.string.api_checkout_picture)));
        }
        if (jObj.has(getResources().getString(R.string.api_checkout_shopname)))
        {
            TextView shopName = (TextView) findViewById(R.id.name_shop);
            shopName.setText(jObj.getString(getResources().getString(R.string.api_checkout_shopname)));
        }
        if (jObj.has(getResources().getString(R.string.api_checkout_transaction)))
        {
            JSONObject jObj2 = new JSONObject(jObj.getString(getResources().getString(R.string.api_checkout_transaction)));
            if (jObj2.has(getResources().getString(R.string.api_checkout_amount)))
            {
                Button payer = (Button) findViewById(R.id.payer);
                payer.setText(getResources().getString(R.string.label_payer)+ " " + jObj2.getString(getResources().getString(R.string.api_checkout_amount)) +" €");
            }
        }
        if (jObj.has(getResources().getString(R.string.api_checkout_products)))
        {
            JSONArray jObj2 = new JSONArray(jObj.getString(getResources().getString(R.string.api_checkout_products)));
            createPreviewTicket(jObj2, jObj);
            Log.d("JOBJ2", ">>>"+jObj2);
        }
    }

    public void createPreviewTicket(JSONArray jObj2, JSONObject jObj) throws JSONException {
        WebView ticket = (WebView) findViewById(R.id.ticket);
        String Sticket = "<html><body style='margin:0px; margin-left:1px;'><table style='width:100%;'><tbody>";
        Sticket += "<tr><td style='text-align:left;'><b>"+getResources().getString(R.string.ticket_designation)+"</b></td><td style='text-align:right;'><b>"+getResources().getString(R.string.ticket_prix)+"</b></td><td style='text-align:right;'><b>"+getResources().getString(R.string.ticket_total)+"</b></td></tr>" ;

        DecimalFormat df = new DecimalFormat("###.##");
        double sousTotal = 0;
        double total = 0;
        double reduction = 0;
        String devise = "€";
        for(int i=0; i<jObj2.length(); i++){
            JSONObject json_data = jObj2.getJSONObject(i);

            double quantity = json_data.getDouble("quantity");
            double price = json_data.getDouble("price");
            double total_product = quantity*price;
            String name_product = json_data.getString("name");
            sousTotal += total_product;
            reduction = 10;
            total = sousTotal - reduction;

            Sticket += "<tr><td style='text-align:left;'>"+String.valueOf(df.format(quantity))+" x "+name_product+"</td><td style='text-align:right;'>"+String.valueOf(df.format(price))+" "+devise+"</td><td style='text-align:right;'>"+String.valueOf(df.format(total_product))+" "+devise+"</td></tr>" ;

        }
        Sticket += "<tr><td><br/>"+getResources().getString(R.string.ticket_sous_total)+" :</td><td></td><td style='text-align: right;'><br/>"+String.valueOf(df.format(sousTotal))+" "+devise+"</td>";

        Sticket += "<tr><td>Remise patate</td><td></td><td style='text-align: right;'>- "+String.valueOf(df.format(reduction))+" "+devise+"</td>";

        Sticket += "<tr><td><span style='font-size: 25px;'><b>"+getResources().getString(R.string.ticket_total_ttc)+" :</b></span></td><td></td><td style='text-align: right;'><span style='font-size: 25px;'><b>"+String.valueOf(df.format(total))+" "+devise+"</b></span></td>";
        Sticket += "</tbody></table><br/>";
        Sticket += "<table style='width:100%; text-align:center;'><tbody><tr><td><b>"+getResources().getString(R.string.ticket_taux)+"</b></td><td><b>"+getResources().getString(R.string.ticket_ht)+"</b></td><td><b>"+getResources().getString(R.string.ticket_tva)+"</b></td><td><b>"+getResources().getString(R.string.ticket_ttc)+"</b></td></tr>";

        Sticket += "<tr><td>5.50 %</td><td>3.30</td><td>0.40</td><td>3.70</td></tr>";
        Sticket += "<tr><td>21 %</td><td>17.00</td><td>3.60</td><td>20.60</td></tr>";

        Sticket += "<tr><td><b>"+getResources().getString(R.string.ticket_total_maj)+"</b></td><td>20.30</td><td>4.00</td><td>24.30</td></tr>";
        Sticket += "</tbody></table>";
        Sticket += "<p>"+getResources().getString(R.string.ticket_by)+" : Charlotte<br/>"+getResources().getString(R.string.ticket_moyen_de_paiement)+" : "+getResources().getString(R.string.app_name)+"<br/><br/>"+getResources().getString(R.string.ticket_date)+" : ";
        if (jObj.has(getResources().getString(R.string.api_checkout_transaction))) {
            JSONObject obj = new JSONObject(jObj.getString(getResources().getString(R.string.api_checkout_transaction)));
            if (obj.has(getResources().getString(R.string.api_checkout_date))) {
                String dateBrut = obj.getString(getResources().getString(R.string.api_checkout_date));
                String[] dateAndHeure = dateBrut.split("T");
                String[] date = dateAndHeure[0].split("-");
                String[] heure = dateAndHeure[1].split(":");
                String[] sec = heure[2].split("\\.");
                Sticket += date[2]+"/"+date[1]+"/"+date[0];
                Sticket += "<br/>"+getResources().getString(R.string.ticket_heure)+" : ";
                Sticket += heure[0]+":"+heure[1]+":"+sec[0];
            }
        }
        Sticket += "<br/>"+getResources().getString(R.string.ticket_ticket)+" : 0000001<br/>"+getResources().getString(R.string.ticket_magasin)+" : 29342</p><p style='text-align:center'>MERCI DE VOTRE VISITE<br/>A BIENTÔT</p></body></html>";
        ticket.loadData(Sticket, "text/html; charset=UTF-8", null);
        ticket.setBackgroundColor(Color.TRANSPARENT);
        ticket.setHapticFeedbackEnabled(false);
        ticket.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        ticket.setLongClickable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return MenuLizz.main_menu(item, getBaseContext(), PayementActivity.this);
    }

    public class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected Bitmap doInBackground(String... args) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap image) {
            if(image != null){
                ImageView img = (ImageView) findViewById(R.id.picture);
                img.setImageBitmap(image);
            }
        }
    }
}
