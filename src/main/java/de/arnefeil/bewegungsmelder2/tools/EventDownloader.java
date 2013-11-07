package de.arnefeil.bewegungsmelder2.tools;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import de.arnefeil.bewegungsmelder2.MainActivity;
import de.arnefeil.bewegungsmelder2.R;
import de.arnefeil.bewegungsmelder2.models.Date;

/**
 * Created by arne on 10/3/13.
 */
public class EventDownloader extends AsyncTask<Void, Void, Boolean> {

    private ProgressDialog progressDialog;
    private MainActivity mainActivity;
    private final String url = "http://192.168.1.17/~arne/bmelderAPI/getEvents.php?downloadEvents";
    //private final String url = "http://10.0.2.2/~arne/bmelderAPI/getEvents.php?downloadEvents";
    //private final String url = "http://www.yomena.com/test/bewegungsmelder/android/getEvents.php?downloadEvents";

    public EventDownloader(MainActivity mainActivity, ProgressDialog progressDialog) {
        this.mainActivity = mainActivity;
        this.progressDialog = progressDialog;
    }

    protected void onPreExecute() {
        mainActivity.setProgressText(
                this.mainActivity.getString(R.string.text_download_update)
        );
    }

    protected Boolean doInBackground(Void... params) {
        int count;
        try {
            URL url = new URL(this.url);
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream is = url.openStream();
            File path = new File(this.mainActivity.getFilesDir().getPath());

            FileOutputStream fos = new FileOutputStream(path + "/events.json");
            byte data[] = new byte[1024];
            while ((count = is.read(data)) != -1) {
                fos.write(data, 0, count);
            }

            is.close();
            fos.close();
            return true;
        } catch (Exception e) {
            Log.v("bmelder", e.getMessage());
            return false;
        }
    }

    protected void onPostExecute(Boolean result) {
        if (result) {
            mainActivity.setProgressText(this.mainActivity.getString(
                    R.string.text_update_success
            ));
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.mainActivity).edit();
            Date today = Date.today();
            String date = today.getYear() + "-" + today.getMonth() + "-" + today.getDay();
            editor.putString("last_sync", date);
            editor.commit();


//            this.mainActivity.showList();
//            this.mainActivity.updateView();
        } else {
            this.progressDialog.setMessage(this.mainActivity.getString(
                    R.string.text_update_error
            ));
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mainActivity.updateEvents();
            }
        }, 1500);
    }


}
