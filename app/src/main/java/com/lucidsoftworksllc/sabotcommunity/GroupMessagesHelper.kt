package com.lucidsoftworksllc.sabotcommunity;

public class GroupMessagesHelper {

    private String id;
    private String user_to;
    private String user_from;
    private String body;
    private String date;

    private String image;
    private String profile_pic;

    public GroupMessagesHelper(String id, String user_to, String user_from, String body, String date, String image, String profile_pic) {
        this.id = id;
        this.user_to = user_to;
        this.user_from = user_from;
        this.body = body;
        this.date = date;
        this.image = image;
        this.profile_pic = profile_pic;
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

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getProfile_pic() {
        return profile_pic;
    }
}
