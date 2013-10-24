package de.arnefeil.bewegungsmelder2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.arnefeil.bewegungsmelder2.models.MyPreference;

/**
 * Created by arne on 10/3/13.
 */
public class SettingActivity extends ActionBarActivity {

    private SettingsAdapter settingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_setting);
        ArrayList<MyPreference> listItems = new ArrayList<MyPreference>();
        listItems.add(new MyPreference(this.getString(R.string.pref_title_sync_frequency), "sync_frequency"));
        listItems.add(new MyPreference(this.getString(R.string.pref_title_last_sync), "last_sync"));
        listItems.add(new MyPreference(this.getString(R.string.pref_title_about), "version"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.settingsAdapter = new SettingsAdapter(this, android.R.layout.simple_list_item_2, listItems);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(this.settingsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyPreference pref = (MyPreference) parent.getItemAtPosition(position);
                if (pref.getTitle().equals(getString(R.string.pref_title_about))) {
                    Intent about = new Intent(SettingActivity.this, AboutActivity.class);
                    startActivity(about);
                }
                if (pref.getTitle().equals(getString(R.string.pref_title_sync_frequency)))
                    createListDialog(pref);
            }
        });
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

    class SettingsAdapter extends ArrayAdapter<MyPreference> {

        private ArrayList<MyPreference> titles;

        public SettingsAdapter(Context context, int resource, ArrayList<MyPreference> objects) {
            super(context, resource, objects);
            this.titles = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v = inflater.inflate(android.R.layout.simple_list_item_2, null);
            }

            MyPreference title = this.titles.get(position);
            TextView text1 = (TextView) v.findViewById(android.R.id.text1);
            TextView text2 = (TextView) v.findViewById(android.R.id.text2);
            if (text1 != null)  {
                text1.setText(title.getTitle());
            }
            if (text2 != null) {
                text2.setText(getTitleToValue(
                        getPreferenceValue(title.getKey())));
            }

            return v;
        }
    }

    private String getPreferenceValue(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (key.equals("sync_frequency"))
            return sharedPreferences.getString(key, "3");
        if (key.equals("last_sync"))
            return sharedPreferences.getString(key, "keins");
        if (key.equals("version")) return "weitere Informationen";

        return sharedPreferences.getString(key, null);
    }

    private String getTitleToValue(String value) {
        String title = value;
        Resources res = getResources();
        String[] values = res.getStringArray(R.array.pref_sync_frequency_values);
        int i;
        for(i=0; i<values.length; i++) {
            if (values[i].equals(value)) {
                title = res.getStringArray(R.array.pref_sync_frequency_titles)[i];
                break;
            }
        }
        return title;
    }

    private String getValueToTile(String title) {
        String value = title;
        Resources res = getResources();
        String[] titles = res.getStringArray(R.array.pref_sync_frequency_titles);
        int i;
        for (i = 0; i < titles.length; i++) {
            if (titles[i].equals(title)) {
                value = res.getStringArray(R.array.pref_sync_frequency_values)[i];
                break;
            }
        }

        return value;
    }

    private void createListDialog(final MyPreference preference) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.simple_listview);
        dialog.setTitle(preference.getTitle());
        dialog.setCancelable(true);
        Rect dialogSize = this.getDialogSize();
        dialog.getWindow().setLayout(dialogSize.width(), dialogSize.height());
        ListView listView = (ListView) dialog.findViewById(R.id.listview);
        Resources res = getResources();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item, res.getStringArray(R.array.pref_sync_frequency_titles));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = (String) parent.getItemAtPosition(position);
                setPreference(preference.getKey(), getValueToTile(title));
                SettingActivity.this.settingsAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void setPreference(String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(key, value);
        editor.commit();
    }

    private Rect getDialogSize() {
        Window window = this.getWindow();
        Rect displ = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(displ);
        Double width = displ.width() * 0.9;
        Double height = displ.height() * 0.7;

        return new Rect(0,0,width.intValue(),height.intValue());
    }
}
