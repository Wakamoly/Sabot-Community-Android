package com.lucidsoftworksllc.sabotcommunity;

import java.util.ArrayList;
import java.util.List;

public class PostComment_Recycler {

    private String id;
    private String body;
    private String posted_by;
    private String posted_to;
    private String time;
    private String post_id;
    private String likes;
    private String liked_by;
    private String user_id;
    private String profile_pic;
    private String nickname;
    private String username;
    private String likedbyuser;
    private String online;
    private String verified;
    private String edited;

    public PostComment_Recycler(String id, String body, String posted_by, String posted_to, String time, String post_id, String likes, String liked_by, String user_id, String profile_pic, String nickname, String username, String likedbyuser, String online, String verified, String edited) {
        this.id = id;
        this.body = body;
        this.posted_by = posted_by;
        this.posted_to = posted_to;
        this.time = time;
        this.post_id = post_id;
        this.likes = likes;
        this.liked_by = liked_by;
        this.user_id = user_id;
        this.profile_pic = profile_pic;
        this.nickname = nickname;
        this.username = username;
        this.likedbyuser = likedbyuser;
        this.online = online;
        this.verified = verified;
        this.edited = edited;
    }

    public String getId() {
        return id;
    }

    public String getOnline() { return online; }

    public String getVerified() { return verified; }

    public String getBody() {
        return body;
    }

    public String getLikedbyuseryes() { return likedbyuser; }

    public String getPosted_by() {
        return posted_by;
    }

    public String getPosted_to() {
        return posted_to;
    }

    public String getTime() {
        return time;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getLikes() {
        return likes;
    }

    public String getLiked_by() {
        return liked_by;
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

    public String getUsername() {
        return username;
    }

    public String getIsEdited() {
        return edited;
    }
}
