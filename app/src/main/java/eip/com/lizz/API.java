package eip.com.lizz;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by guillaume on 20/12/14.
 */
public class API {

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public static boolean isOnline(Activity context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public static JSONObject prepareJson(int responseCode, InputStream inputStream, JSONObject jObj, int responseCodeElse) throws Exception {
        String result = "";
        if(inputStream != null)
        {
            result = API.convertStreamToString(inputStream);
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

}
