package eip.com.lizz;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

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
import java.net.CookieStore;
import java.util.List;

/**
 * Created by guillaume on 21/12/14.
 */
public class APIlogout extends AsyncTask<Void, Void, JSONObject> {

    private final String mtokenCSFR;
    private final Context contextHere;
    private List<Cookie> mCookies;

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

    APIlogout(String tokenCSFR, Context context) {
        contextHere = context;
        mtokenCSFR = tokenCSFR;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject jObj = null;
        String url_api = contextHere.getResources().getString(R.string.url_api_final_v1)+"session/destroy";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.setCookieStore(new BasicCookieStore());

        mCookies = Cookies.loadSharedPreferencesCookie(contextHere);
       /* if (mCookies.isEmpty()) {
            System.out.println("LOGOUT None");
        } else {
            for (int i = 0; i < mCookies.size(); i++) {
                System.out.println("LOGOUT - " + mCookies.get(i).toString());
            }
        } */
        httpClient.getCookieStore().addCookie(mCookies.get(0));
        HttpPost httppost = new HttpPost(url_api);
        String json;
        InputStream inputStream = null;
        try {
            JSONObject data = new JSONObject();
            try {
                data.put("_csrf", mtokenCSFR);
            } catch (JSONException e) {
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
            switch(responseCode) {
                case 200:
                    jObj = API.prepareJson(responseCode, inputStream, jObj, 202);
                    break;
                default:
                    jObj = API.prepareJson(responseCode, inputStream, jObj, 403);
                    break;
            }
        } catch (ClientProtocolException e) {
            try {
                jObj = API.prepareJson(408, null, jObj, 408);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;
    }

    @Override
    protected void onPostExecute(final JSONObject jObj) {
        this._task_finished_event.OnTaskFihishedEvent(jObj);
    }

    public static void checkErrorsAndLaunch(JSONObject jObj, Context context) {
        try {
           if(jObj != null)
           {
               if (jObj.get("responseCode").toString().equals("200"))
               {
                   Log.d("ALLLOOOOOOO", ">>> WORLSE");
                   Intent loggedUser = new Intent(context, HomeActivity.class);
                   loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                   context.startActivity(loggedUser);
               }
               else
               {
                   Log.d("ALLLOOOOOOO", ">>> ON A BIEN DELETE LE COOKIE");
               }
           }
            else
           {
               Log.d("ALLLOOOOOOO", ">>> ON A BIEN DELETE LE COOKIE 22222");
               Intent loggedUser = new Intent(context, HomeActivity.class);
               loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
               context.startActivity(loggedUser);
           }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}