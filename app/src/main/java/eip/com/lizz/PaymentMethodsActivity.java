package eip.com.lizz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PaymentMethodsActivity extends ActionBarActivity { // Retrocompatibilité Remettre ActionBArActivity et enlever android: dans le style (en attente de compréhension
    // Attributes
    private RecyclerView                mRecyclerView;
    private RecyclerView.Adapter        mAdapter;
    private RecyclerView.LayoutManager  mLayoutManager;

    private Button                      mCheckInternetButton;

    // Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        String[] myDataset = {"Nom du compte", "Nom du compte", "Nom du compte"};

        mAdapter = new PaymentMethodsAdapter(myDataset, this);
        mRecyclerView.setAdapter(mAdapter);

        // Binding des touches
        mCheckInternetButton = (Button) findViewById(R.id.checkInternetButton);
        mCheckInternetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    Log.d("Network", "Network is on");
                    new queryGetPaymentMethodList().execute("http://163.5.84.225:8000/api/v1/" + "user/getPaymentCards");
                } else {
                    Log.d("Network", "Network is off");
                }
            }
        });
    }

    private class queryGetPaymentMethodList extends AsyncTask <String, Void, String> {

        public String inputStringToStream(InputStream stream) throws IOException, UnsupportedEncodingException {
            /*Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[stream.];
            reader.read(buffer);
            return new String(buffer);*/
            return stream.toString();
        }

        private String downloadURL(String myUrl) throws IOException {
            InputStream mInputStream = null;

            try {
                URL url = new URL(myUrl);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.setReadTimeout(10000); // C'est quoi ?
                connection.setConnectTimeout(15000); // C'est quoi ?
                connection.setRequestMethod("GET");
                connection.setDoInput(true); // C'est quoi ?

                connection.connect();
                int responseConnect = connection.getResponseCode();
                Log.d("Connection", "The response code is " + responseConnect);
                mInputStream = connection.getInputStream();

                // Convert InputStream to String
                String resultString = inputStringToStream(mInputStream);
                return resultString;

//                return "placeHolderString";
            } finally {
                if (mInputStream != null)
                    mInputStream.close();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadURL(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_payment_methods, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_add_item) {
            Intent intent = new Intent(this, AddEditPaymentMethodActivity.class);
            intent.putExtra("EXTRA_TYPE", "add");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

