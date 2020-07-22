package com.lucidsoftworksllc.sabotcommunity;

class ChatroomMessagesHelper {
    private final String id;
    private final String message;
    private final String username;
    private final String time_message;
    private final String nickname;
    private final String profile_pic;
    private final String last_online;
    private final String user_level;
    private final String verified;
    private final String userid;

    public ChatroomMessagesHelper(String id, String message, String username, String time_message, String nickname, String profile_pic, String last_online, String user_level, String verified, String userid) {
        this.id = id;
        this.message = message;
        this.username = username;
        this.time_message = time_message;
        this.nickname = nickname;
        this.profile_pic = profile_pic;
        this.last_online = last_online;
        this.user_level = user_level;
        this.verified = verified;
        this.userid = userid;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public String getTime_message() {
        return time_message;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getlast_online() {
        return last_online;
    }

    public String getUser_level() {
        return user_level;
    }

    public String getVerified() {
        return verified;
    }

    public String getUserid() {
        return userid;
    }
}
