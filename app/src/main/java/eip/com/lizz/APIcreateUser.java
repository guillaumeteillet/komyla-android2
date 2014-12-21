package eip.com.lizz;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by guillaume on 20/12/14.
 */
public class APIcreateUser extends AsyncTask<Void, Void, JSONObject> {

    private final String mFirstname;
    private final String mSurname;
    private final String mEmail;
    private final String mPassword;
    private final String mtokenCSFR;
    private final Activity contextHere;
    private final List<Cookie> mCookies;

    private OnTaskExecutionFinished _task_finished_event;

    public interface OnTaskExecutionFinished
    {
        public void OnTaskFihishedEvent(JSONObject jObj);
    }

    public void setOnTaskFinishedEvent(OnTaskExecutionFinished _event)
    {
        if(_event != null)
        {
            this._task_finished_event = _event;
        }
    }

    APIcreateUser(String firstname, String surname, String email, String password, String tokenCSFR, Activity context, List<Cookie> cookies) {
        mFirstname = firstname;
        mSurname = surname;
        mEmail = email;
        mPassword = password;
        contextHere = context;
        mtokenCSFR = tokenCSFR;
        mCookies = cookies;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject jObj = null;
        String url_api = contextHere.getResources().getString(R.string.url_api_final_v1)+"user/create";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.setCookieStore(new BasicCookieStore());

        httpClient.getCookieStore().addCookie(mCookies.get(0));
        HttpPost httppost = new HttpPost(url_api);
        String json;
        InputStream inputStream = null;
        try {
            JSONObject data = new JSONObject();
            try {
                data.put("_csrf", mtokenCSFR);
                data.put("firstname", mFirstname);
                data.put("surname", mSurname);
                data.put("email", mEmail);
                data.put("password", mPassword);
                data.put("passwordConfirmation", mPassword);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            json = data.toString();
            StringEntity se = new StringEntity(json);
            httppost.setEntity(se);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httppost);
            inputStream = httpResponse.getEntity().getContent();

            int responseCode = httpResponse.getStatusLine().getStatusCode();
            Log.d("APPEL API", ">>"+responseCode);
            switch(responseCode) {
                case 200:
                    jObj = prepareJson(responseCode, inputStream, jObj, 202);
                    break;
                case 400:
                    jObj = prepareJson(responseCode, inputStream, jObj, 400);
                    break;
                case 403:
                    jObj = prepareJson(responseCode, inputStream, jObj, 403);
                    break;
                default:
                    jObj = prepareJson(responseCode, inputStream, jObj, 500);
                    break;
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;
    }

    private JSONObject prepareJson(int responseCode, InputStream inputStream, JSONObject jObj, int responseCodeElse) throws Exception {
        String result = "";
        if(inputStream != null)
        {
            result = API.convertStreamToString(inputStream);
            Log.d("APIIIII", ">>>"+result);
            if (!result.equals("") && !result.equals("CSRF mismatch"))
            {
                jObj = new JSONObject(result);
            }
            else
                jObj = new JSONObject();
            jObj.put("responseCode",responseCode);
        }
        else
        {
            jObj = new JSONObject();
            jObj.put("responseCode", responseCodeElse);
        }
        return jObj;
    }

    @Override
    protected void onPostExecute(final JSONObject jObj) {
        this._task_finished_event.OnTaskFihishedEvent(jObj);
    }
}