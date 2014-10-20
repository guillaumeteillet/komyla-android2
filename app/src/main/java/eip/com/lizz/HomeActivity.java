package eip.com.lizz;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class HomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView fb = (ImageView) findViewById(R.id.btnConnectFB);
        fb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Facebook Connect", Toast.LENGTH_LONG).show();
            }
        });

        ImageView gplus = (ImageView) findViewById(R.id.btnConnectGPlus);
        gplus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Google Plus Connect", Toast.LENGTH_LONG).show();
            }
        });

        Button lizzConnect = (Button) findViewById(R.id.btnConnectLizz);
        lizzConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Button lizzNewAccount = (Button) findViewById(R.id.btnNewAccountLizz);
        lizzNewAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
