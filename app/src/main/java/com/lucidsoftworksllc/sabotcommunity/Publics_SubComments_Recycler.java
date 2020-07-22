package com.lucidsoftworksllc.sabotcommunity;

public class Publics_SubComments_Recycler {
    public String getOnline() {
        return online;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getReply() {
        return reply;
    }

    public String getPost_date() {
        return post_date;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUserid() {
        return userid;
    }

    public String getUsername() {
        return username;
    }

    public String getVerified() {
        return verified;
    }

    private final String online;
    private final String post_id;
    private final String reply;
    private final String post_date;
    private final String profile_pic;
    private final String nickname;
    private final String userid;
    private final String username;
    private final String verified;
    private String clantag;

    public Publics_SubComments_Recycler(String online, String post_id, String reply, String post_date, String profile_pic, String nickname, String userid, String username, String verified, String clantag) {


        this.online = online;
        this.post_id = post_id;
        this.reply = reply;
        this.post_date = post_date;
        this.profile_pic = profile_pic;
        this.nickname = nickname;
        this.userid = userid;
        this.username = username;
        this.verified = verified;
        this.clantag = clantag;
    }

    public String getClantag() {
        return clantag;
    }
}
