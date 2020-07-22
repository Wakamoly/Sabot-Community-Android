package com.lucidsoftworksllc.sabotcommunity;

import java.util.ArrayList;
import java.util.List;

public class Notifications_Recycler {

    private String id, user_to, user_from, message, type, link, datetime, opened, viewed, user_id, profile_pic, nickname, verified, last_online;

    public Notifications_Recycler(String id, String user_to, String user_from, String message, String type, String link, String datetime, String opened, String viewed, String user_id, String profile_pic, String nickname, String verified, String last_online) {
        this.id = id;
        this.user_to = user_to;
        this.user_from = user_from;
        this.message = message;
        this.type = type;
        this.link = link;
        this.datetime = datetime;
        this.opened = opened;
        this.viewed = viewed;
        this.user_id = user_id;
        this.profile_pic = profile_pic;
        this.nickname = nickname;
        this.verified = verified;
        this.last_online = last_online;
    }

    public String getId() {
        return id;
    }

    public String getUser_to() {
        return user_to;
    }

    public String getUser_from() {
        return user_from;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getLink() {
        return link;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getOpened() {
        return opened;
    }

    public String getViewed() {
        return viewed;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getNickname() {
        return nickname;
    }

    public String getVerified() {
        return verified;
    }

    public String getLast_online() {
        return last_online;
    }

   /* public static List<Publics_Recycler> createPublics(int itemCount) {
        List<Publics_Recycler> npPublics = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Publics_Recycler newPublics = new Publics_Recycler(itemCount  + (itemCount == 0 ?
                    (itemCount + 1 + i) : (itemCount + i)), "yeeetskeet", "yelllooo", "dang.jpg");


            npPublics.add(newPublics);
        }
        return npPublics;
    }*/
}
