package de.arnefeil.bewegungsmelder.models;

/**
 * Created by arne on 10/3/13.
 */
public class Link {

    private String url;
    private String title;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Link) {
            Link l = (Link) o;
            result = this.getTitle().equals(l.getTitle())
                    && this.getUrl().equals(l.getUrl());
        }

        return result;
    }
}
