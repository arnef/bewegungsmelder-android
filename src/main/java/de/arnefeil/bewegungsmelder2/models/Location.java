package de.arnefeil.bewegungsmelder2.models;

import java.util.List;

/**
 * Created by arne on 10/3/13.
 */
public class Location implements Comparable<Location> {

    private String title;
    private String description;
    private List<Link> links;
    private List<String> categories;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public List<Link> getLinks() {
        return this.links;
    }

    public List<String> getCategories() {
        return this.categories;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Location) {
            Location location = (Location) o;
            result = this.getTitle().equals(location.getTitle());
            if (this.getDescription() != null)
                result &= this.getDescription().equals(location.getDescription());
            else { if (location.getDescription() != null) return false; }
            if (this.getCategories() != null)
                result &= this.getCategories().equals(location.getCategories());
            else { if (location.getCategories() != null) return false; }
            if (this.getLinks() != null)
                result &= this.getLinks().equals(location.getLinks());
            else { if (location.getLinks() != null) return false; }
        }
        return result;
    }

    @Override
    public int compareTo(Location another) {
        return this.getTitle().compareToIgnoreCase(another.getTitle());
    }
}
