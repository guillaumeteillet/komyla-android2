package eip.com.lizz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.ImageScanner;

/**
 * Created by guillaume on 16/12/14.
 */
public class MenuLizz {

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
            return signout(context);
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

    private static boolean signout(Context context)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putBoolean("eip.com.lizz.isLogged", false).apply();
        Intent loggedUser = new Intent(context, HomeActivity.class);
        loggedUser.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(loggedUser);
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
}
