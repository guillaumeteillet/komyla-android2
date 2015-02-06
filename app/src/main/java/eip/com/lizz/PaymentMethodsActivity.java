package eip.com.lizz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.concurrent.ExecutionException;

import eip.com.lizz.QueriesAPI.GetPaymentMethodsFromAPI;

public class PaymentMethodsActivity extends ActionBarActivity {
// Retrocompatibilité Remettre ActionBarActivity et enlever android: dans le style

    /* Attributes */
    private RecyclerView                mRecyclerView;
    private RecyclerView.Adapter        mAdapter;
    private RecyclerView.LayoutManager  mLayoutManager;

    private Button                      mCheckInternetButton;

    /* Methods */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

       /* Get data */
        GetPaymentMethodsFromAPI task = new GetPaymentMethodsFromAPI(this);
        try {
            String result = task.execute().get();
            Log.v("GetPaymentMethods", "Voici le résultat de la requête : " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        String[] myDataset = {"Nom du compte", "Nom du compte", "Nom du compte",
                "Nom du compte", "Nom du compte", "Nom du compte"};

        /* Creation of the design */
        Bindings();
        createRecyclerView(myDataset);
    }

    private void Bindings() {
        /*mCheckInternetButton = (Button) findViewById(R.id.checkInternetButton);
        mCheckInternetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    Log.d("Network", "Network is on");
                    new GetPaymentMethodsFromAPI().execute("http://163.5.84.225:8000/api/v1/" + "user/getPaymentCards");
                } else {
                    Log.d("Network", "Network is off");
                }
            }
        });*/
    }

    private void createRecyclerView(String[] myDataset) {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PaymentMethodsAdapter(myDataset, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_payment_methods, menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

       if (id == R.id.action_add_item) {
            Intent intent = new Intent(this, AddEditPaymentMethodActivity.class);
            intent.putExtra("EXTRA_TYPE", "add");
            startActivity(intent);
            return true;
        }

        return MenuLizz.main_menu(item, getBaseContext(), PaymentMethodsActivity.this);
    }
}

