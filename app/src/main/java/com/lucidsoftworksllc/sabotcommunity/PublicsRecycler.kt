package com.lucidsoftworksllc.sabotcommunity;

public class Publics_Recycler {

    private String id;
    private String title;
    private String genre;
    private String image;
    private String numratings;
    private String avgrating;
    private String tag;
    private String postcount;
    private String followed;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setNumratings(String numratings) {
        this.numratings = numratings;
    }

    public void setAvgrating(String avgrating) {
        this.avgrating = avgrating;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setPostcount(String postcount) {
        this.postcount = postcount;
    }

    public String getId() {
        return id;
    }

    public String getNumratings(){ return numratings; }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getImage() {
        return image;
    }

    public String getAvgrating() {
        return avgrating;
    }

    public String getTag() {
        return tag;
    }

    public String getPostcount() {
        return postcount;
    }

    public void setFollowed(String followed) {
        this.followed = followed;
    }

    public String getFollowed() {
        return followed;
    }
}
