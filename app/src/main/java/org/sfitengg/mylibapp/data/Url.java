package org.sfitengg.mylibapp.data;

public class Url {
    private final String url;
    private final String name;

    //name is the name of that list view in inflated recyclerView.

    public Url(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }
}
