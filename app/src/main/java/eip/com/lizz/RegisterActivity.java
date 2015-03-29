package eip.com.lizz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eip.com.lizz.QueriesAPI.AddUserToApi;
import eip.com.lizz.QueriesAPI.GetCsrfFromAPI;
import eip.com.lizz.QueriesAPI.LogUserToAPI;
import eip.com.lizz.Utils.UAlertBox;
import eip.com.lizz.Utils.UApi;
import eip.com.lizz.Utils.USaveParams;

public class RegisterActivity extends Activity implements LoaderCallbacks<Cursor>{

    private GetCsrfFromAPI mAuthTask = null;
    private AddUserToApi mAuthTask2 = null;
    private LogUserToAPI mAuthTask3 = null;
    private AutoCompleteTextView mEmailView;
    private EditText mSurnameView;
    private EditText mFirstnameView;
    private EditText mPhoneNumber;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mAuthTask = null;
        mAuthTask2 = null;

        TelephonyManager tMgr = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = tMgr.getLine1Number();
        mFirstnameView = (EditText) findViewById(R.id.firstname);
        mSurnameView = (EditText) findViewById(R.id.name);
        mPhoneNumber = (EditText) findViewById(R.id.phone);
        if (getResources().getString(R.string.debugOrProd).equals("DEBUG"))
            phoneNumber = "0";

        if (phoneNumber != null)
            mPhoneNumber.setText(phoneNumber);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuthTask = null;
                mAuthTask2 = null;
                            if (UApi.isOnline(RegisterActivity.this))
                                attemptLogin();
                            else
                                UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.code000));
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (VERSION.SDK_INT >= 14) {
            getLoaderManager().initLoader(0, null, this);
        } else if (VERSION.SDK_INT >= 8) {
            new SetupEmailAutoCompleteTask().execute(null, null);
        }
    }

    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mFirstnameView.setError(null);
        mSurnameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        final String firstname = mFirstnameView.getText().toString();
        final String surname = mSurnameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(firstname)) {
            mFirstnameView.setError(getString(R.string.error_field_required));
            focusView = mFirstnameView;
            cancel = true;
        }

        else if (TextUtils.isEmpty(surname)) {
            mSurnameView.setError(getString(R.string.error_field_required));
            focusView = mSurnameView;
            cancel = true;
        }

        else if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            final EditText mdpConfirm = new EditText(RegisterActivity.this);
            mdpConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
            final AlertDialog.Builder alert = UAlertBox.alertInputOk(RegisterActivity.this, getResources().getString(R.string.dialog_title_confirm), getResources().getString(R.string.dialog_confirm_mdp), mdpConfirm);
            alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {


                    if (mdpConfirm.getText().toString().equals(mPasswordView.getText().toString())) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mdpConfirm.getWindowToken(), 0);
            View view = getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            showProgress(true);

            mAuthTask = new GetCsrfFromAPI(RegisterActivity.this);
            mAuthTask.setOnTaskFinishedEvent(new GetCsrfFromAPI.OnTaskExecutionFinished()
            {
                @Override
                public void OnTaskFihishedEvent(final String tokenCSFR, final List<Cookie> cookies)
                {
                    if (tokenCSFR.equals("000x000"))
                    {
                        UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.code000));
                    }
                    else
                    {
                        mAuthTask2 = new AddUserToApi(firstname, surname, email, password, tokenCSFR, RegisterActivity.this, cookies);
                        mAuthTask2.setOnTaskFinishedEvent(new AddUserToApi.OnTaskExecutionFinished() {
                            @Override
                            public void OnTaskFihishedEvent(JSONObject jObj) {
                                showProgress(false);
                                try {
                                    if (jObj.get("responseCode").toString().equals("200"))
                                    {
                                        finish();
                                        mAuthTask3 = new LogUserToAPI(mEmailView.getText().toString(), mPasswordView.getText().toString(), tokenCSFR, RegisterActivity.this, cookies);
                                        mAuthTask3.setOnTaskFinishedEvent(new LogUserToAPI.OnTaskExecutionFinished() {
                                            @Override
                                            public void OnTaskFihishedEvent(JSONObject jObj) {

                                                showProgress(false);
                                                LogUserToAPI.checkErrorsAndLaunch(jObj, RegisterActivity.this, getBaseContext());
                                            }
                                        });
                                        mAuthTask3.execute();
                                    }
                                    else if (jObj.get("responseCode").toString().equals("400"))
                                    {
                                        JSONObject array = jObj.getJSONObject("invalidAttributes");
                                        boolean firstnameIsMissing = array.has("firstname");
                                        boolean surnameIsMissing = array.has("surname");
                                        boolean emailIsMissing = array.has("email");
                                        boolean passwordIsMissing = array.has("password");
                                        if (firstnameIsMissing)
                                            UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code002));
                                        else if (surnameIsMissing)
                                            UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code003));
                                        else if (emailIsMissing)
                                            UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code004));
                                        else if (passwordIsMissing)
                                            UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code005));
                                    }
                                    else if(jObj.get("responseCode").toString().equals("403"))
                                    {
                                        UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code001));
                                    }
                                    else if(jObj.get("responseCode").toString().equals("408"))
                                    {
                                        UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.code000));
                                    }
                                    else if (jObj.get("responseCode").toString().equals("500"))
                                    {
                                        UAlertBox.alertOk(RegisterActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.error_server_ok_but_fail) + getResources().getString(R.string.code006));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                        mAuthTask2.execute();
                    }
                }

            });
            mAuthTask.execute();

                    }
                    else
                    {
                        USaveParams.displayError(5, RegisterActivity.this, null, null, false);
                        mdpConfirm.setText("");
                    }
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.dialog_cancel), null);
            alert.show();
        }
    }

    private boolean isEmailValid(String email) {
        if (email.contains("@") && email.contains("."))
            return true;
        else
            return false;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                                                                     .CONTENT_ITEM_TYPE},

                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            ArrayList<String> emailAddressCollection = new ArrayList<String>();

            ContentResolver cr = getContentResolver();
            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    null, null, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
                        .CommonDataKinds.Email.DATA));
                emailAddressCollection.add(email);
            }
            emailCur.close();

            return emailAddressCollection;
        }

	    @Override
	    protected void onPostExecute(List<String> emailAddressCollection) {
	       addEmailsToAutoComplete(emailAddressCollection);
	    }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }
}



