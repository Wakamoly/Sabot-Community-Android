package com.lucidsoftworksllc.sabotcommunity;

public class ConvosHelper {
    private String sent_by;
    private String body_split;
    private String time_message;
    private String latest_profile_pic;
    private String profile_pic;
    private String nickname;
    private String verified;
    private String last_online;
    private String viewed;
    private String id;
    private String user_from;
    private String type;
    private String group_id;

    public ConvosHelper(String sent_by, String body_split, String time_message, String latest_profile_pic, String profile_pic, String nickname, String verified, String last_online, String viewed, String id, String user_from, String type, String group_id) {
        this.sent_by = sent_by;
        this.body_split = body_split;
        this.time_message = time_message;
        this.latest_profile_pic = latest_profile_pic;
        this.profile_pic = profile_pic;
        this.nickname = nickname;
        this.verified = verified;
        this.last_online = last_online;
        this.viewed = viewed;
        this.id = id;
        this.user_from = user_from;
        this.type = type;
        this.group_id = group_id;
    }

    public String getLatest_profile_pic() {
        return latest_profile_pic;
    }

    public String getSent_by() {
        return sent_by;
    }

    public String getBody_split() {
        return body_split;
    }

    public String getTime_message() {
        return time_message;
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

    public String getViewed() {
        return viewed;
    }

    public String getId() {
        return id;
    }

    public String getUser_from() {
        return user_from;
    }

    public String getType() {
        return type;
    }

    public String getGroup_id() {
        return group_id;
    }
}