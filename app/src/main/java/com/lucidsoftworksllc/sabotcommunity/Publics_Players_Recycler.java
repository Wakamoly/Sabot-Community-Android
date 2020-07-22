package com.lucidsoftworksllc.sabotcommunity;

class Publics_Players_Recycler {
    private String id;
    private String profile_pic;
    private String nickname;
    private String userid;
    private String username;
    private String topicID;
    private String request;

    public Publics_Players_Recycler(String id, String profile_pic, String nickname, String userid, String username, String topicID, String request) {
        this.id = id;
        this.profile_pic = profile_pic;
        this.nickname = nickname;
        this.userid = userid;
        this.username = username;
        this.topicID = topicID;
        this.request = request;
    }

    public String getId() {
        return id;
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

    public String getTopicID() {
        return topicID;
    }

    public String getUserPosition() {
        return request;
    }
}
