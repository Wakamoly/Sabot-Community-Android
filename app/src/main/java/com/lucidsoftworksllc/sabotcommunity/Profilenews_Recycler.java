package com.lucidsoftworksllc.sabotcommunity;

public class Profilenews_Recycler {

    private int id;
    private String type;
    private String likes;
    private String body;
    private String added_by;
    private String user_to;
    private String date_added;
    private String user_closed;
    private String deleted;
    private String image;
    private String user_id;
    private String profile_pic;
    private String verified;
    private String online;
    private String nickname;
    private String username;
    private String commentcount;
    private String likedbyuseryes;
    private String form;
    private String edited;

    public Profilenews_Recycler(int id, String type, String likes, String body, String added_by, String user_to, String date_added, String user_closed, String deleted, String image, String user_id, String profile_pic, String verified, String online, String nickname, String username, String commentcount, String likedbyuseryes, String form, String edited) {
        this.id = id;
        this.type = type;
        this.likes = likes;
        this.body = body;
        this.added_by = added_by;
        this.user_to = user_to;
        this.date_added = date_added;
        this.user_closed = user_closed;
        this.deleted = deleted;
        this.image = image;
        this.user_id = user_id;
        this.profile_pic = profile_pic;
        this.verified = verified;
        this.online = online;
        this.nickname = nickname;
        this.username = username;
        this.commentcount = commentcount;
        this.likedbyuseryes = likedbyuseryes;
        this.form = form;
        this.edited = edited;
    }

    public String getForm() {
        return form;
    }

    public int getId() {
        return id;
    }

    public String getType() { return type; }

    public String getLikes() {
        return likes;
    }

    public String getBody() {
        return body;
    }

    public String getAdded_by() {
        return added_by;
    }

    public String getUser_to() {
        return user_to;
    }

    public String getDate_added() {
        return date_added;
    }

    public String getUser_closed() {
        return user_closed;
    }

    public String getDeleted() {
        return deleted;
    }

    public String getImage() {
        return image;
    }

    public String getUser_id() { return user_id; }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getVerified() { return verified; }

    public String getOnline() { return online; }

    public String getNickname() {
        return nickname;
    }

    public String getUsername() {
        return username;
    }

    public String getCommentcount() {
        return commentcount;
    }

    public String getLikedbyuseryes() { return  likedbyuseryes; }

    public String getIsEdited() {
        return edited;
    }
}
