package com.lucidsoftworksllc.sabotcommunity;

public class Search_Recycler {

    private String type, id, name, subname, image, extra, numratings;

    public Search_Recycler(String type, String id, String name, String subname, String image, String extra, String numratings) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.subname = subname;
        this.image = image;
        this.extra = extra;
        this.numratings = numratings;
    }

    public String getType() { return type; }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSubname() {
        return subname;
    }

    public String getImage() {
        return image;
    }

    public String getExtra() {
        return extra;
    }

    public String getNumratings() {
        return numratings;
    }
}
