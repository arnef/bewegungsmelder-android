package de.arnefeil.bewegungsmelder.models;

import java.util.List;

/**
 * Created by arne on 10/3/13.
 */
public class Band implements Comparable<Band>{

    private String title;
    private String description;
    private List<Link> links;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
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

    @Override
    public String toString() {
        return this.title;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Band) {
            Band b = (Band) o;
            result = this.getTitle().equals(b.getTitle())
                    && this.getDescription().equals(b.getDescription())
                    && this.getLinks().equals(b.getLinks());
        }

        return result;
    }

    @Override
    public int compareTo(Band another) {
        return this.getTitle().compareToIgnoreCase(another.getTitle());
    }
}

