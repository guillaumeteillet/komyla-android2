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

    public static void checkParentalControlStatus(final Activity context, final String params, final String key, final boolean isForChangePIN)
    {

        SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        final String parentalCode = sharedpreferences.getString("eip.com.lizz.parentalcontrolpassword", "");
        final Boolean statusParentalControl = sharedpreferences.getBoolean("eip.com.lizz.parentalcontrolstatus", false);

        if (statusParentalControl)
        {
            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            final AlertDialog.Builder alert = AlertBox.alertInputOk(context, context.getResources().getString(R.string.dialog_title_confirm), context.getResources().getString(R.string.dialog_confirm_hint_pin), input);
            alert.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (input.getText().toString().equals(parentalCode))
                    {
                        checkIsForChangePinOrNot(isForChangePIN, context, key, params);
                    }
                    else
                    {
                        displayError(1, context, params, key, isForChangePIN);
                    }
                }
            });
            alert.show();
        }
        else
        {
            checkIsForChangePinOrNot(isForChangePIN, context, key, params);
        }

    }

    public static void checkIsForChangePinOrNot(final Boolean isForChangePIN, final Activity context, final String key, final String params)
    {
        if(isForChangePIN)
        {

            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            final AlertDialog.Builder alert = AlertBox.alertInputOk(context, context.getResources().getString(R.string.dialog_title_confirm), context.getResources().getString(R.string.dialog_confirm_hint_pin_change), input);
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
        else
            saveParamsString(context, key, params);
    }

    public static void saveParamsString(Activity context, String key, String value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putString(key, value).apply();
        context.finish();
    }

    public static void saveParamsBoolean(Activity context, String key, Boolean value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        sharedpreferences.edit().putBoolean(key, value).apply();
        context.finish();
    }

    public static void displayError(int i, final Activity context, final String params, final String key, final boolean isForChangePIN) {

        switch (i)
        {
            case 1:
                AlertDialog.Builder alertDialogBuilder = AlertBox.alert(context, context.getResources().getString(R.string.error),context.getResources().getString(R.string.wrongPassword));
                alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                checkParentalControlStatus(context, params, key, isForChangePIN);
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            case 2:
                AlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.emptyPasswordParental));
                break;
            case 3:
                AlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.wrongPasswordConfirm));
                break;
            case 4:
                AlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.wrongOldPassword));
                break;
            case 5:
                AlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.wrongPassword));
                break;
            case 6:
                AlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.emptyPasswordPIN));
                break;
            case 7:
                AlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.wrongRIB));
                break;
            case 8:
                AlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.IBANisNotFrench));
                break;
            default:
                AlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.errordefault));
                break;

        }
    }
}
