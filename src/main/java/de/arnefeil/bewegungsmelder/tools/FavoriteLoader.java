package de.arnefeil.bewegungsmelder.tools;

import android.util.Log;

import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;

import de.arnefeil.bewegungsmelder.MainActivity;
import de.arnefeil.bewegungsmelder.models.Event;

/**
 * Created by arne on 10/3/13.
 */
public class FavoriteLoader {

    private MainActivity mainActivity;
    private HashSet<Event> favorites;


    public FavoriteLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.favorites = new HashSet<Event>();
    }

    public void toggleFavorites() {
        if (!this.mainActivity.getEventLoader().isFavorited()) {
            if (mainActivity.getEventLoader().isFiltered()) {
                this.setFavorites(this.mainActivity.getEventLoader().getEventListFiltered());
                this.mainActivity.getEventLoader().setEventListFavorites(this.getFavorites());
            } else {
                this.setFavorites(mainActivity.getEventLoader().getEventList());
                this.mainActivity.getEventLoader().setEventListFavorites(this.getFavorites());
            }
            this.mainActivity.getEventLoader().setFavorited(true);
        } else {
            this.mainActivity.getEventLoader().setFavorited(false);
        }
        this.mainActivity.changeFavIcon();
        this.mainActivity.updateView();
        this.saveFavorites();
    }

    public void setFavorites(ArrayList<Event> events) {
        this.favorites = new HashSet<Event>();
        if (events != null) {
            for (Event e: events) {
                if (e.isFavorite()) this.favorites.add(e);
            }
        }
    }

    public ArrayList<Event> getFavorites() {
        return new ArrayList<Event>(this.favorites);
    }

    public void saveFavorites() {
        this.setFavorites(mainActivity.getEventLoader().getEventList());
        HashSet<Integer> favIds = new HashSet<Integer>();
        for (Event e: this.favorites) {
            favIds.add(e.getId());
        }
        JSONArray jsonArray = new JSONArray(favIds);
        try {
            File path = new File(this.mainActivity.getFilesDir().getPath());
            FileWriter file = new FileWriter(path + "/favorites.json");
            file.write(jsonArray.toString());
            file.flush();
            file.close();
        } catch (Exception e) {
            Log.v("bmelder", e.getMessage());
        }
    }
}
