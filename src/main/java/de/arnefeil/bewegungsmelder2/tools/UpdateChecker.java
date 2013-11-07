package de.arnefeil.bewegungsmelder2.tools;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.MessageDigest;

import de.arnefeil.bewegungsmelder2.MainActivity;
import de.arnefeil.bewegungsmelder2.R;
import de.arnefeil.bewegungsmelder2.models.Date;

/**
 * Created by arne on 10/3/13.
 */
public class UpdateChecker extends AsyncTask<Void, Void, String> {

    private MainActivity mainActivity;
    //private final String url = "http://192.168.1.17/~arne/bmelderAPI/getEvents.php?updateCheck";
    //private final String url = "http://10.0.2.2/~arne/bmelderAPI/getEvents.php?updateCheck";
    private final String url = "http://www.yomena.com/test/bewegungsmelder/android/getEvents.php?updateCheck";

    public UpdateChecker(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private void checkForUpdate(String webMd5) {
        File file = new File(this.mainActivity.getFilesDir() + "/events.json");
        if (file.exists()) {
            Log.v("bmelder", this.md5sum(file));
            if (!this.md5sum(file).equals(webMd5)) {
                new EventDownloader(this.mainActivity).execute();
            } else {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.mainActivity).edit();
                Date today = Date.today();
                String date = today.getYear() + "-" + today.getMonth() + "-" + today.getDay();
                editor.putString("last_sync", date);
                editor.commit();
                mainActivity.setProgressText(this.mainActivity.getString(
                        R.string.text_no_update_available));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.showList();
                    }
                }, 1500);
            }
        } else {
            new EventDownloader(this.mainActivity).execute();
        }
    }

    private String md5sum(File file) {
        InputStream is = null;
        byte[] md5Bytes = null;
        try {
            is = new FileInputStream(file.getAbsolutePath());
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead;
            while ((numRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, numRead);
            }
            md5Bytes = digest.digest();
            is.close();
        } catch (Exception e) {
            Log.v("bmelder", e.getMessage());
        }

        return this.convertHashToString(md5Bytes);
    }

    private String convertHashToString(byte[] md5Bytes) {
        String returnVal = "";
        for (int i=0; i < md5Bytes.length; i++) {
            returnVal += Integer.toString((md5Bytes[i] & 0xff) + 0x100, 16).substring(1);
        }

        return returnVal;
    }

    protected void onPreExecute() {
        this.mainActivity.showProgress();
        this.mainActivity.setProgressText(
                this.mainActivity.getString(R.string.text_search_for_update)
        );
    }

    protected String doInBackground(Void... params) {
        try {
            String result;
            BufferedReader in;

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setSoTimeout(client.getParams(), 20000);
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 20000);

            URI web = new URI(this.url);
            HttpGet request = new HttpGet();
            request.setURI(web);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String l = "";
            while ((l = in.readLine()) != null) {
                sb.append(l);
            }
            in.close();
            result = sb.toString();

            return result;
        } catch (Exception e) {
            Log.v("bmelder", e.getMessage());

            return null;
        }


    }

    protected void onPostExecute(String result) {
        if (result != null) {
            this.checkForUpdate(result);
        } else {
            this.mainActivity.setProgressText(this.mainActivity.getString(
                    R.string.text_update_error
            ));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mainActivity.showList();
                }
            }, 1500);
        }
    }
}

