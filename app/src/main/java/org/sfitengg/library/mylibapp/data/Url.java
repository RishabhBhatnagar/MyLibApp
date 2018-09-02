package org.sfitengg.library.mylibapp.data;

public class Url {
    private String url;
    private String name;

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
