package com.lucidsoftworksllc.sabotcommunity;

public class CurrentPublicsPOJO {

    private String id;
    private String subject;
    private String catname;
    private String type;
    private String profile_pic;
    private String nickname;
    private String event_date;
    private String context;
    private String num_players;
    private String playing_now;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getNum_players() {
        return num_players;
    }

    public void setNum_players(String num_players) {
        this.num_players = num_players;
    }

    public String getNum_added() {
        return num_added;
    }

    public void setNum_added(String num_added) {
        this.num_added = num_added;
    }

    private String num_added;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPlayingNow(String playing_now) {this.playing_now = playing_now;}

    public String getPlaying_now(){return playing_now;}
}
