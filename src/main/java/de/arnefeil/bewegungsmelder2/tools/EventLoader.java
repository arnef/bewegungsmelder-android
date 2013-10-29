package de.arnefeil.bewegungsmelder2.tools;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.arnefeil.bewegungsmelder2.MainActivity;
import de.arnefeil.bewegungsmelder2.models.Band;
import de.arnefeil.bewegungsmelder2.models.Date;
import de.arnefeil.bewegungsmelder2.models.Event;
import de.arnefeil.bewegungsmelder2.models.Link;
import de.arnefeil.bewegungsmelder2.models.Location;
import de.arnefeil.bewegungsmelder2.models.Time;

/**
 * Created by arne on 10/3/13.
 */
public class EventLoader extends AsyncTask<Void,Void,ArrayList<Event>> {

    private JSONArray events;
    private JSONArray favorites;
    private boolean filtered;
    private boolean favorited;
    private ArrayList<Event> eventList;
    private ArrayList<Event> eventListFiltered;
    private ArrayList<Event> eventListFavorites;
    private MainActivity context;

    public EventLoader(MainActivity context) {
        this.eventList = new ArrayList<Event>();
        this.context = context;
//        this.execute();
    }

    public ArrayList<Event> update() {
        this.filtered = false;
        this.eventList.clear();
        try {
            File fav = new File(this.context.getFilesDir() + "/favorites.json");
            FileInputStream fis;
            String jString = null;
            if (fav.exists()) {
                fis = new FileInputStream(fav);
                try {
                    FileChannel fc = fis.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                    jString = Charset.defaultCharset().decode(bb).toString();
                    Log.v("bmelder", "favorites: " + jString);
                } finally {
                    fis.close();
                }
                this.favorites = new JSONArray(jString);
            }

            File file = new File(this.context.getFilesDir() + "/events.json");
            fis = new FileInputStream(file);
            jString = null;
            try {
                FileChannel fc = fis.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                jString = Charset.defaultCharset().decode(bb).toString();
                Log.v("bmelder", "events: " + jString);
            } finally {
                fis.close();
            }

            this.events = new JSONArray(jString);
            return this.readJSONArray();
        } catch (Exception e) {
            Log.v("bmelder", e.getMessage());
        }
        return null;
    }

    public ArrayList<Event> getEvents(Date date) {
        ArrayList<Event> dayList = new ArrayList<Event>();
        ArrayList<Event> events = this.eventList;
        if (this.filtered) events = this.eventListFiltered;
        if (this.favorited) events = this.eventListFavorites;
        if (events != null) {
        for (Event e: events) {
            if (e.getDate().equals(date))
                dayList.add(e);
        }}

        return dayList;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public boolean isFiltered() {
        return this.filtered;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean isFavorited() {
        return this.favorited;
    }

    public ArrayList<Event> getEventList() {
        return this.eventList;
    }

    public ArrayList<Event> getEventListFiltered() {
        return this.eventListFiltered;
    }

    public void setEventListFiltered(ArrayList<Event> events) {
        this.eventListFiltered = events;
    }

    public void setEventListFavorites(ArrayList<Event> events) {
        this.eventListFavorites = events;
    }

    public ArrayList<Date> getDates() {
        Set<Date> dates = new TreeSet<Date>();
        ArrayList<Event> events = this.eventList;
        if (this.filtered) events = this.eventListFiltered;
        if (this.favorited) events = this.eventListFavorites;
        if (events == null || events.size() == 0) dates.add(Date.today());
        else for (Event e: events)
            dates.add(e.getDate());

        return new ArrayList<Date>(dates);
    }

    private ArrayList<Event> readJSONArray() {
        ArrayList<Event> events = new ArrayList<Event>();
        try {
            ArrayList<Integer> favs = new ArrayList<Integer>();
            if (this.favorites != null) {
                for (int i = 0; i < this.favorites.length(); i++) {
                    favs.add(this.favorites.getInt(i));
                }
            }

            for (int i = 0; i < this.events.length(); i++) {
                JSONObject jo = this.events.getJSONObject(i);
                Event e = new Event();
                e.setId(jo.getInt("id"));
                e.setTitle(jo.getString("title"));
                e.setCancelled(jo.getBoolean("canceled"));
                e.setFavorite(favs.contains(e.getId()));
                if (!jo.isNull("description"))
                    e.setDescription(jo.getString("description"));
                if (!jo.isNull("more_infos"))
                    e.setDescriiptionExtras(jo.getString("more_infos"));
                if (!jo.isNull("price"))
                    e.setPrice(jo.getString("price"));
                if (!jo.isNull("timeEntry"))
                    e.setTimeEntry(new Time(jo.getString("timeEntry")));
                if (!jo.isNull("timeStart"))
                    e.setTimeStart(new Time(jo.getString("timeStart")));
                else e.setTimeStart(Time.allDayTime());
                if (!jo.isNull("bands"))
                    e.setBands(this.parseBands(jo.getJSONArray("bands")));
                if (!jo.isNull("location"))
                    e.setLocation(this.parseLocation(jo.getJSONObject("location")));
                if (!jo.isNull("date"))
                    e.setDate(new Date(jo.getString("date")));
                if (!jo.isNull("links"))
                    e.setLinks(this.parseLinks(jo.getJSONArray("links")));
                if (!jo.isNull("type"))
                    e.setType(this.parseCategories(jo.getJSONArray("type")));
                events.add(e);
            }
        } catch (Exception e) {
            Log.v("bmelder", e.getMessage());
        }

        return events;
    }


    private List<Band> parseBands(JSONArray bands) throws Exception {
        List<Band> bandList = new ArrayList<Band>();
        for (int i = 0; i < bands.length(); i++) {
            JSONObject b = bands.getJSONObject(i);
            Band band = new Band();
            band.setTitle(b.getString("title"));
            if (!b.isNull("description"))
                band.setDescription(b.getString("description"));
            if (!b.isNull("links")) {
                band.setLinks(this.parseLinks(b.getJSONArray("links")));
            }
            bandList.add(band);
        }
        return bandList;
    }

    private List<Link> parseLinks(JSONArray links) throws Exception {
        List<Link> linkList = new ArrayList<Link>();
        for (int i = 0; i < links.length(); i++) {
            JSONObject l = links.getJSONObject(i);
            Link link = new Link();
            link.setTitle(l.getString("title"));
            link.setUrl(l.getString("url"));
            linkList.add(link);
        }

        return linkList;
    }

    private List<String> parseCategories(JSONArray categories) throws Exception {
        List<String> categoryList = new ArrayList<String>();
        for (int i = 0; i < categories.length(); i++) {
            categoryList.add(categories.getString(i));
        }

        return categoryList;
    }

    private Location parseLocation(JSONObject location) throws Exception {
        Location loc = new Location();
        loc.setTitle(location.getString("title"));
        if (!location.isNull("description"))
            loc.setDescription(location.getString("description"));
        if (!location.isNull("links"))
            loc.setLinks(this.parseLinks(location.getJSONArray("links")));
        if (!location.isNull("category"))
            loc.setCategories(this.parseCategories(location.getJSONArray("category")));

        return loc;
    }

    protected void onPreExecute() {
        context.showProgress();
    }

    @Override
    protected ArrayList<Event> doInBackground(Void... params) {
        return this.update();
    }

    protected void onPostExecute(ArrayList<Event> events) {
        this.eventList = events;
        context.initFilerLoader();
        context.showList();
        context.updateView();
    }
}

