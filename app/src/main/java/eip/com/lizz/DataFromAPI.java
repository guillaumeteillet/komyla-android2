package eip.com.lizz;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by guillaume on 19/10/2014.
 */
public class DataFromAPI extends AsyncTask<String, Void, JSONObject> {

        private String url = "http://private-anon-ca973bc2f-lizz.apiary-mock.com/";

        private final Context _context;

        public DataFromAPI(Context context) {
            this._context = context;

            Log.d("API", "WTF");
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();

            Log.d("API", "Params > "+params.length);
            if (params.length == 0)
                return null;

            url = url + params[0];
            Log.d("API", "URL>"+url);
            HttpGet httpget = new HttpGet(url);
            HttpResponse response;
            try {
                response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                Log.d("API", "OK TRY");

                if (entity != null) {
                    InputStream instream = entity.getContent();
                    String result = convertStreamToString(instream);

                    JSONObject jObj = new JSONObject(result);


                    instream.close();
                    Log.d("API", "OK IF");
                    return jObj;
                }
            } catch (JSONException e) {
                Log.d("API", "JsonException");
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                Log.d("API", "ClientProtocolException");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("API", "IOException");
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("API", "Exception");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject aJSon) {
            super.onPostExecute(aJSon);
        }

        private static String convertStreamToString(InputStream is) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            return sb.toString();
        }

}
