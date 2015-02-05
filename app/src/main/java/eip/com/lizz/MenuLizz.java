package eip.com.lizz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.MenuItem;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by guillaume on 16/12/14.
 */
public class MenuLizz {

    private static APIlogout mAuthTask;
    static CookieStore cookieStore;
    static ProgressDialog dialog;
    static Handler handler = null;

    public static boolean main_menu(MenuItem item, Context context)
    {
        int id = item.getItemId();
        if (id == R.id.action_params) {
            return settings(context);
        }
        if (id == R.id.action_signout) {
            return signout(context);
        }
        return false;
    }

    public static boolean settings_menu(MenuItem item, Context context) {
        int id = item.getItemId();
        if (id == R.id.action_signout) {
            return signout(context);
        }
        return false;
    }

    public static boolean scan_menu(MenuItem item, Activity context) {
        int id = item.getItemId();
        if (id == R.id.action_flash) {
            return onOrOffFlash(item, context);
        }
        if (id == R.id.action_cant_scan) {
            return cant_scan(context);
        }
        return false;
    }

    public static boolean unique_code_menu(MenuItem item, Activity context) {
        int id = item.getItemId();
        if (id == R.id.action_scan) {
            return scan(context);
        }
        return false;
    }


    // Options des menus

    private static boolean settings(Context context)
    {
        Intent loggedUser = new Intent(context, SettingsActivity.class);
        loggedUser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(loggedUser);
        return true;
    }

    public static boolean signout(final Context context)
    {
        String token;
        SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        token = sharedpreferences.getString("eip.com.lizz._csrf", "");
        sharedpreferences.edit().putBoolean("eip.com.lizz.isLogged", false).apply();
        mAuthTask = new APIlogout(token, context);
        mAuthTask.setOnTaskFinishedEvent(new APIlogout.OnTaskExecutionFinished() {
            @Override
            public void OnTaskFihishedEvent(JSONObject jObj) {
                APIlogout.checkErrorsAndLaunch(jObj, context);
            }

        });
        mAuthTask.execute();
        return true;
    }

    private static boolean onOrOffFlash(MenuItem item, Activity context) {
            SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
            Boolean flash = sharedpreferences.getBoolean("eip.com.lizz.flash", false);
            if (flash)
            {
                sharedpreferences.edit().putBoolean("eip.com.lizz.flash", false).apply();
            }
            else
            {
                sharedpreferences.edit().putBoolean("eip.com.lizz.flash", true).apply();
            }
            context.finish();
            Intent loggedUser = new Intent(context, ScanQRCodeActivity.class);
            context.startActivity(loggedUser);
        return true;
    }

    private static boolean cant_scan(Activity context) {
        Intent cantscan = new Intent(context, PayementWithUniqueCodeActivity.class);
        context.startActivity(cantscan);
        return true;
    }

    private static boolean scan(Activity context) {
        Intent scan = new Intent(context, ScanQRCodeActivity.class);
        context.startActivity(scan);
        context.finish();
        return true;
    }
}
