package eip.com.lizz.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Environment;

import eip.com.lizz.APISendFile;
import eip.com.lizz.AlertBox;
import eip.com.lizz.R;

/**
 * Created by guillaume on 15/02/15.
 */
public class UThread {

    public static APISendFile mAuthTask = null;

    public static void send(final Activity context, final String file_name, String pleasewait, final String OK) {


        final ProgressDialog progress = ProgressDialog.show(context, context.getResources().getString(R.string.pleasewait), pleasewait, true);
    new Thread(new Runnable() {
        @Override
        public void run()
        {
            mAuthTask = new APISendFile(context, file_name, "http://teillet.eu/lizz/index.php", progress);
            mAuthTask.setOnTaskFinishedEvent(new APISendFile.OnTaskExecutionFinished() {
                @Override
                public void OnTaskFihishedEvent(Boolean jObj) {
                    AlertDialog.Builder alert;
                    alert = AlertBox.alert(context, context.getResources().getString(R.string.labelDocumentOK), OK);
                    alert.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            context.finish();
                        }
                    });
                    alert.show();
                }
            });
            mAuthTask.execute();
        }
    }).start();
    }
}
