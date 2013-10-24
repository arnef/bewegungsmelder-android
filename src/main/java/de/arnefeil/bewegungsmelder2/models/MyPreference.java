package de.arnefeil.bewegungsmelder2.models;

/**
 * Created by arne on 10/5/13.
 */
public class MyPreference {

    private String title;
    private String key;

    public MyPreference(String title, String key) {
        this.title = title;
        this.key = key;
    }

    public String getTitle() {
        return this.title;
    }

    public String getKey() {
        return this.key;
    }
}
