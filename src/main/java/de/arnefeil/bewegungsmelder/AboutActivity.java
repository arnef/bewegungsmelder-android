package de.arnefeil.bewegungsmelder;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            String version = this.getPackageManager().getPackageInfo(this.getPackageName(),0).versionName;
            TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
            tvVersion.setText("Version " + version);
        } catch (Exception e) { Log.v("bmelder", e.getMessage()); }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return false;
        }
    }
}
