package eip.com.lizz;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeActivity extends FragmentActivity implements View.OnClickListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String fbAccessToken;
    public String fbLastName;
    public String fbId;
    public String fbFirstName;
    public String email;
    private UserRegisterSSO mAuthTask = null;
    private static final String TAG = "ExampleActivity";
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LoginButton authButton = (LoginButton) findViewById(R.id.btnConnectFB);
        authButton.setReadPermissions(Arrays.asList("email", "user_friends", "user_birthday", "user_likes"));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

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

        SignInButton gplus = (SignInButton) findViewById(R.id.plus_sign_in_button);
        if (!supportsGooglePlayServices()) {
            gplus.setVisibility(View.GONE);
            return;
        }
        else
        {
            gplus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d("API", "CLICK");
                    mSignInClicked = true;
                    resolveSignInError();
                }
            });

        }
    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }


    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }


    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void onClick(View view) {
        Log.d("API", "CLICK");
        if (view.getId() == R.id.plus_sign_in_button
                && !mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }
    


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
        else
        {

        Session session = Session.getActiveSession();
        session.onActivityResult(this, requestCode, resultCode, data);
        if (session.isOpened()) {
            fbAccessToken = session.getAccessToken();
            // make request to get facebook user info
            RequestAsyncTask requestAsyncTask = Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {

                    // ID Unique FB, LastName, FirstName, Email (OBLIGATOIRE)

                    fbId = user.getId();
                    fbLastName = user.getLastName();
                    fbFirstName = user.getFirstName();
                    email = user.asMap().get("email").toString();

                    // Sexe, Birth

                    String fbSex = user.asMap().get("gender").toString();
                    String fbBirthday = user.asMap().get("birthday").toString();

                    // TO DO : Likes, UserFriends


                    //DEBUG FB
                    Log.i("fb", "fb user: " + user.toString());
                    Log.i("fb", ">>>>" + fbFirstName + "--" + fbLastName + "--" + fbId + "---" + fbAccessToken + "---" + email + "---" + fbSex + "--" + fbBirthday);

                    // TO DO : Verifier si le token Facebook existe déjà ou non

                    boolean account_exist = true;

                    mAuthTask = new UserRegisterSSO(fbFirstName, fbLastName, email, "", account_exist);
                    mAuthTask.execute((Void) null);

                    SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                    sharedpreferences.edit().putBoolean("eip.com.lizz.isLogged", true).apply();

                    Session session = Session.getActiveSession();
                    if (session != null) {
                        session.closeAndClearTokenInformation();
                    }

                    Intent loggedUser = new Intent(getBaseContext(), HomeLizzActivity.class);
                    loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);// On supprime les vues précédentes, l'utilisateur est connecté.
                    //loggedUser.putExtra("user_info",jObj.toString());
                    startActivity(loggedUser);
                }
            });
        }
        }
    }

    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        // TO DO : Verifier si le token Google+ existe déjà ou non

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            fbFirstName = String.valueOf(currentPerson.getName().getGivenName());
            fbLastName = String.valueOf(currentPerson.getName().getFamilyName());
            email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Log.d("TEST", ">>>"+fbFirstName+"--"+fbLastName+"----"+email);
        }

        boolean account_exist = true;
        mAuthTask = new UserRegisterSSO(fbFirstName, fbLastName, email, "", account_exist);
        mAuthTask.execute((Void) null);
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
        SharedPreferences sharedpreferences = getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putBoolean("eip.com.lizz.isLogged", true).apply();
        Intent loggedUser = new Intent(getBaseContext(), HomeLizzActivity.class);
        loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);// On supprime les vues précédentes, l'utilisateur est connecté.
        //loggedUser.putExtra("user_info",jObj.toString());
        startActivity(loggedUser);
    }

    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterSSO extends AsyncTask<Void, Void, Boolean> {

        private final String mFirstname;
        private final String mSurname;
        private final String mEmail;
        private final String mPassword;
        private final Boolean mAccountExist;

        UserRegisterSSO(String firstname, String surname, String email, String password, Boolean account_exist) {
            mFirstname = firstname;
            mSurname = surname;
            mEmail = email;
            mPassword = password;
            mAccountExist = account_exist;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            String url_api;
            if (mAccountExist)
                url_api = getResources().getString(R.string.url_api_login);
            else
                url_api = getResources().getString(R.string.url_api_create);

            HttpPost httppost = new HttpPost(url_api);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("fisrtname", mFirstname));
                nameValuePairs.add(new BasicNameValuePair("surname", mSurname));
                nameValuePairs.add(new BasicNameValuePair("password", ""));
                nameValuePairs.add(new BasicNameValuePair("email", mEmail));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int responseCode = response.getStatusLine().getStatusCode();
                switch(responseCode) {
                    case 200:
                        HttpEntity entity = response.getEntity();
                        if(entity != null) {
                            String responseBody = EntityUtils.toString(entity);
                            Log.d("REGISTER","<<<<"+mFirstname+"-"+mSurname+"-"+mPassword+"-"+mEmail);
                            Log.d("REGISTER","<<<<"+responseBody);
                        }
                        break;
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
   private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }
}
