package eip.com.lizz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by guillaume on 06/02/15.
 */
public class APISendFile extends AsyncTask<String, Void, Boolean> {

    private final Activity contextHere;
    private final String mUrl;
    private final String mapi;
    private final ProgressDialog mProgress;

    private OnTaskExecutionFinished _task_finished_event;

    public interface OnTaskExecutionFinished
    {
        public void OnTaskFihishedEvent(Boolean jObj);
    }

    public void setOnTaskFinishedEvent(OnTaskExecutionFinished _event)
    {
        if(_event != null)
        {
            this._task_finished_event = _event;
        }
    }

    public APISendFile(final Activity context, String url, String api, ProgressDialog progress) {
        contextHere = context;
        mUrl = url;
        mProgress = progress;
        mapi = api;
    }

    protected Boolean doInBackground(String... urls) {
        try {
            String tokenCSFR;
            SharedPreferences sharedpreferences = contextHere.getSharedPreferences("eip.com.lizz", Context.MODE_PRIVATE);
            tokenCSFR = sharedpreferences.getString("eip.com.lizz._csrf", "");

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            final String dir = mUrl;
            File sourceFile = new File(dir);

            if (!sourceFile.isFile()) {
                mProgress.dismiss();
                AlertBox.alertOk(contextHere, contextHere.getResources().getString(R.string.error), contextHere.getResources().getString(R.string.errordefault));
                return false;
            }
            else
            {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(mapi); // Adresse Temporaire, en attende l'API
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", dir);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename='"+ dir + "'" + lineEnd);
                dos.writeBytes(lineEnd);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                int serverResponseCode = conn.getResponseCode();
                if(serverResponseCode == 200){
                    contextHere.runOnUiThread(new Runnable() {
                        public void run() {
                            mProgress.dismiss();
                        }
                    });
                }
                fileInputStream.close();
                dos.flush();
                dos.close();

                return true;
            }
        } catch (Exception e) {
            mProgress.dismiss();
            AlertBox.alertOk(contextHere, contextHere.getResources().getString(R.string.error), contextHere.getResources().getString(R.string.errordefault));
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean jObj) {
        this._task_finished_event.OnTaskFihishedEvent(jObj);
    }
}

