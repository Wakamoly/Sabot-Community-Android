package com.lucidsoftworksllc.sabotcommunity;

class MessageUserListRecycler {
    private final String id;
    private final String user_id;
    private final String profile_pic;
    private final String nickname;
    private final String username;
    private final String verified;
    private final String online;
    private final String desc;
    private final String position;
    private final String owner;
    private final String canRemove;
    private final String queryID;

    public MessageUserListRecycler(String id, String user_id, String profile_pic, String nickname, String username, String verified, String online, String desc, String position, String owner, String canRemove, String queryID) {
        this.id = id;
        this.user_id = user_id;
        this.profile_pic = profile_pic;
        this.nickname = nickname;
        this.username = username;
        this.verified = verified;
        this.online = online;
        this.desc = desc;
        this.position = position;
        this.owner = owner;
        this.canRemove = canRemove;
        this.queryID = queryID;
    }

    public String getId() {
        return id;
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

    public String getVerified() {
        return verified;
    }

    public String getOnline() {
        return online;
    }

    public String getDesc() {
        return desc;
    }

    public String getPosition() {
        return position;
    }

    public String getOwner() {
        return owner;
    }

    public String getCanRemove() {
        return canRemove;
    }

    public String getGroupID() {
        return queryID;
    }
}
