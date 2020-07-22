package com.lucidsoftworksllc.sabotcommunity;

class Clan_Members_Recycler {
    private final String id;
    private final String profile_pic;
    private final String nickname;
    private final String userid;
    private final String username;
    private final String clanid;
    private final String clantag;
    private final String position;

    public Clan_Members_Recycler(String id, String profile_pic, String nickname, String userid, String username, String clanid, String clantag, String position) {
        this.id = id;
        this.profile_pic = profile_pic;
        this.nickname = nickname;
        this.userid = userid;
        this.username = username;
        this.clanid = clanid;
        this.clantag = clantag;
        this.position = position;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public String getId() {
        return id;
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

    public String getClanid() {
        return clanid;
    }

    public String getClantag() {
        return clantag;
    }

    public String getUserPosition() {
        return position;
    }
}
