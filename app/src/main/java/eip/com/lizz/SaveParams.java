package eip.com.lizz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by guillaume on 12/12/14.
 */
public class SaveParams {

    public static void askPassword(final Activity context, final String params, final String key, final boolean isForChangePIN)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(context.getResources().getString(R.string.dialog_title_confirm));
        if (isForChangePIN)
            alert.setMessage(context.getResources().getString(R.string.dialog_confirm_hint_pin_change));
        else
            alert.setMessage(context.getResources().getString(R.string.dialog_confirm_hint_pin));
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        alert.setView(input);
        alert.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
                String codePin = sharedpreferences.getString("eip.com.lizz.codepinlizz", "");
                if (input.getText().toString().equals(codePin))
                {
                    saveParamsString(context, key, params);
                }
                else
                {
                    displayError(1, context, params, key, isForChangePIN);
                }
            }
        });
        alert.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), null);
        alert.show();
    }

    private static void saveParamsString(Activity context, String key, String value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putString(key, value).apply();
        context.finish();
    }

    public static void displayError(int i, final Activity context, final String params, final String key, final boolean isForChangePIN) {

        if (i == 1)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.error));
            alertDialogBuilder
                    .setMessage(context.getResources().getString(R.string.wrongPassword))
                    .setCancelable(false)
                    .setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            askPassword(context, params, key, isForChangePIN);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}
