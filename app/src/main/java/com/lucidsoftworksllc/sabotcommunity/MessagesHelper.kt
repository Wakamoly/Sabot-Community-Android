package com.lucidsoftworksllc.sabotcommunity;

public class MessagesHelper {

    private String id;
    private String user_to;
    private String user_from;
    private String body;
    private String date;

    private String image;

    public MessagesHelper(String id, String user_to, String user_from, String body, String date, String image) {
        this.id = id;
        this.user_to = user_to;
        this.user_from = user_from;
        this.body = body;
        this.date = date;
        this.image = image;
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
}
