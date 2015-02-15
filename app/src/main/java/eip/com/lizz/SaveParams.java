package eip.com.lizz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;

/**
 * Created by guillaume on 12/12/14.
 */
public class SaveParams {

    public static void checkIsForChangePinOrNot(final Boolean isForChangePIN, final Activity context, final String key, final String params)
    {
        final SharedPreferences sharedpreferences = context.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
        final String codePin = sharedpreferences.getString("eip.com.lizz.codepinlizz", "");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        input.setTextColor(Color.BLACK);
        final AlertDialog.Builder alert = AlertBox.alertInputOk(context, context.getResources().getString(R.string.dialog_title_confirm), context.getResources().getString(R.string.dialog_confirm_hint_pin_change), input);
        alert.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.getText().toString().equals(codePin))
                {
                    final EditText input2 = new EditText(context);
                    input2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    input2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    input2.setTextColor(Color.BLACK);
                    final AlertDialog.Builder alertnew = AlertBox.alertInputOk(context, context.getResources().getString(R.string.dialog_title_confirm), context.getResources().getString(R.string.dialog_confirm_hint_newpin), input2);
                    alertnew.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (input2.getText().toString().equals(params))
                            {
                                saveParamsString(context, key, params);
                                sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", 0).apply();
                            }
                            else
                                displayError(1, context, params, key, true);
                        }
                    });
                    alertnew.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), null);
                    alertnew.show();
                }
                else
                    tentativeCheck(context, sharedpreferences);
            }

        });
        alert.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), null);

        final AlertDialog.Builder alert2 = AlertBox.alertInputOk(context, context.getResources().getString(R.string.dialog_title_confirm), context.getResources().getString(R.string.dialog_confirm_hint_pin), input);
        alert2.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.getText().toString().equals(codePin))
                {
                    saveParamsString(context, key, params);
                    sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", 0).apply();
                }
                else
                    tentativeCheck(context, sharedpreferences);
            }

        });
        alert2.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), null);


        if(isForChangePIN)
            alert.show();
        else
            alert2.show();
    }

    public static void tentativeCheck(Activity context, SharedPreferences sharedpreferences)
    {
        int tentatives = sharedpreferences.getInt("eip.com.lizz.tentativePin", 0);
        int new_tentative = tentatives + 1;
        sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", new_tentative).apply();
        if (new_tentative == 1)
            displayError(2, context, "2", null, true);
        else if (new_tentative == 2)
            displayError(2, context, "1", null, true);
        else if (new_tentative == 3)
        {
            sharedpreferences.edit().putInt("eip.com.lizz.tentativePin", 0).apply();
            MenuLizz.logout(context);
        }
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

    // DEV : Merci d'utiliser les codes d'erreur libre avant d'en créer de nouveau...

    public static void displayError(int i, final Activity context, final String params, final String key, final boolean isForChangePIN) {

        switch (i)
        {
            case 1:
                AlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_new_pincode));
                break;
            case 2:
                AlertBox.alertOk(context, context.getResources().getString(R.string.error_tentatives), context.getResources().getString(R.string.error_nb_tentatives)+" "+params+" "+context.getResources().getString(R.string.tentatives));
                break;
            case 3:
                break;
            case 4:
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
            case 9 :
                AlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.error_pin));
                break;
            default:
                AlertBox.alertOk(context, context.getResources().getString(R.string.error), context.getResources().getString(R.string.errordefault));
                break;

        }
    }
}
