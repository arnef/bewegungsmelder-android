package de.arnefeil.bewegungsmelder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.arnefeil.bewegungsmelder.adapter.EventAdapter;
import de.arnefeil.bewegungsmelder.models.Date;
import de.arnefeil.bewegungsmelder.models.Event;
import de.arnefeil.bewegungsmelder.tools.EventLoader;
import de.arnefeil.bewegungsmelder.tools.FavoriteLoader;
import de.arnefeil.bewegungsmelder.tools.FilterLoader;
import de.arnefeil.bewegungsmelder.tools.UpdateChecker;

public class MainActivity extends ActionBarActivity {

    private EventLoader eventLoader;
    private FilterLoader filterLoader;
    private FavoriteLoader favoriteLoader;
    private Menu menu;
    private ArrayList<Date> dates;
    private int[] dialogSize;
    private FrameLayout progessView;
    private TextView mTextViewProgress;
    ViewPager viewPager;
    EventsPageAdapter eventsPageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.viewPager = (ViewPager) findViewById(R.id.pager);
        this.progessView = (FrameLayout) findViewById(R.id.fl_loading_circle);
        this.mTextViewProgress = (TextView) findViewById(R.id.tv_progress);
        showProgress();

        this.eventLoader = new EventLoader(this);
        //this.dates = this.eventLoader.getDates();
        this.dates = new ArrayList<Date>();
        this.dates.add(Date.today());
        this.filterLoader = new FilterLoader(this);
        this.favoriteLoader = new FavoriteLoader(this);

        this.eventsPageAdapter = new EventsPageAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(this.eventsPageAdapter);
        this.switchToPage(Date.today());
        this.getSupportActionBar().setTitle("Events");
        this.checkUpdateTimer();
        this.setDialogSize();
        this.eventLoader.execute();
    }


    public void updateEvents() {
        this.eventLoader = new EventLoader(this);
        eventLoader.execute();
    }

    public void setProgressText(String message) {
        mTextViewProgress.setText(message);
    }

    public void showProgress() {
        this.viewPager.setVisibility(View.GONE);
        this.progessView.setVisibility(View.VISIBLE);
    }

    public void showList() {
        this.viewPager.setVisibility(View.VISIBLE);
        this.progessView.setVisibility(View.GONE);
    }

    public void initFilerLoader() {
        this.filterLoader = new FilterLoader(this);
    }

    public void updateView() {
        this.viewPager.setCurrentItem(0);
        this.dates = this.eventLoader.getDates();
        this.viewPager.getAdapter().notifyDataSetChanged();
        this.switchToPage(Date.today());
    }
    public void updateList() {
        this.viewPager.getAdapter().notifyDataSetChanged();
    }

    public void changeFavIcon() {
        MenuItem item = this.menu.findItem(R.id.action_favorites);
        if (this.eventLoader.isFavorited())
            item.setIcon(R.drawable.rating_favorited);
        else
            item.setIcon(R.drawable.rating_favorite);
    }

    private void switchToPage(Date date) {
        for (int i = 0; i < this.dates.size(); i++) {
            if (this.dates.get(i).equals(date)) {
                this.viewPager.setCurrentItem(i, true);
                break;
            }
        }
    }

    private void setDialogSize() {
        Window window = this.getWindow();
        Rect displ = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(displ);
        Double width = displ.width() * 0.9;
        Double height = displ.height() * 0.7;
        this.dialogSize = new int[2];
        this.dialogSize[0] = width.intValue();
        this.dialogSize[1] = height.intValue();
    }

    public int[] getDialogSize() {
        return this.dialogSize;
    }

    public EventLoader getEventLoader() {
        return this.eventLoader;
    }

    private void checkUpdateTimer() {
        SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(this);
        String lastSync = manager.getString("last_sync", "keins");
        String syncFreq = manager.getString("sync_frequency", "3");
        int sync = 0;
        try { sync = Integer.parseInt(syncFreq); } catch (Exception e) {}
        if (sync != -1) {
            if (lastSync.equals("keins"))
                new UpdateChecker(this).execute();
            else {
                Date lastSyncDate = new Date(lastSync).dateInDays(sync);
                int com = lastSyncDate.compareTo(Date.today());
                if (com < 1)
                    new UpdateChecker(this).execute();
            }
        }
    }

    private void selectDateDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.simple_listview);
        dialog.setTitle(this.getString(R.string.action_select_date));
        dialog.setCancelable(true);
        if (this.dates.size() < 5)
            dialog.getWindow().setLayout(this.getDialogSize()[0],
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        else
            dialog.getWindow().setLayout(this.getDialogSize()[0],
                    this.getDialogSize()[1]);

        ListView lvDateList = (ListView) dialog.findViewById(R.id.listview);
        ArrayAdapter<Date> adapter = new ArrayAdapter<Date>(this,
                android.R.layout.select_dialog_item, this.dates);
        lvDateList.setAdapter(adapter);
        lvDateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Date date = (Date) parent.getItemAtPosition(position);
                MainActivity.this.switchToPage(date);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_date:
                this.selectDateDialog();
                return true;
            case R.id.action_update_db:
                new UpdateChecker(this).execute();
                return true;
            case R.id.action_settings:
                Intent setting = new Intent(this, SettingActivity.class);
                startActivity(setting);
                return true;
            case R.id.action_filter:
                this.filterLoader.execute();
                return true;
            case R.id.action_favorites:
                this.favoriteLoader.toggleFavorites();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.favoriteLoader.saveFavorites();
    }
    


    public class EventsPageAdapter extends FragmentPagerAdapter {


        public EventsPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new EventsSectionFragment();
            Bundle args = new Bundle();
            args.putInt(EventsSectionFragment.ARG_SECTION_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return MainActivity.this.dates.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return MainActivity.this.dates.get(position).toString();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @SuppressLint("ValidFragment")
    public class EventsSectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);
            Date date = MainActivity.this.dates.get(getArguments().getInt(ARG_SECTION_NUMBER));
            ArrayList<Event> events = MainActivity.this.getEventLoader().getEvents(date);
            ListView lvEvents = (ListView) rootView.findViewById(R.id.listView);
            lvEvents.setAdapter(new EventAdapter(MainActivity.this, R.layout.listview_event, events));
            return rootView;
        }

    }
}
