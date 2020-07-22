package com.lucidsoftworksllc.sabotcommunity;

public class PlayerReview_Recycler {

    private String ratingnumber;
    private String title;
    private String comments;
    private String reply;
    private String time;
    private String profile_pic;
    private String nickname;
    private String user_id;

    public PlayerReview_Recycler(String ratingnumber, String title, String comments, String reply, String time, String profile_pic, String nickname, String user_id) {
        this.ratingnumber = ratingnumber;
        this.title = title;
        this.comments = comments;
        this.reply = reply;
        this.time = time;
        this.profile_pic = profile_pic;
        this.nickname = nickname;
        this.user_id = user_id;
    }

    public String getRatingnumber() {
        return ratingnumber;
    }

    public String getTitle() {
        return title;
    }

    public String getComments() {
        return comments;
    }

    public String getReply() {
        return reply;
    }

    public String getTime() {
        return time;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUser_id() {
        return user_id;
    }
}
