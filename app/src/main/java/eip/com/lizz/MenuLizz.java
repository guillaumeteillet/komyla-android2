package eip.com.lizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;

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
}
