package com.lucidsoftworksllc.sabotcommunity;

public class PublicsTopic_Recycler {

    private String id;
    private String numposts;
    private String subject;
    private String date;
    private String cat;
    private String topic_by;
    private String type;
    private String user_id;
    private String profile_pic;
    private String nickname;
    private String username;
    private String event_date;
    private String zone;
    private String context;
    private String num_players;
    private String num_added;
    private String gamename;

    public PublicsTopic_Recycler(String id, String numposts, String subject, String date, String cat, String topic_by, String type, String user_id, String profile_pic, String nickname, String username, String event_date, String zone, String context, String num_players, String num_added, String gamename) {
        this.id = id;
        this.numposts = numposts;
        this.subject = subject;
        this.date = date;
        this.cat = cat;
        this.topic_by = topic_by;
        this.type = type;
        this.user_id = user_id;
        this.profile_pic = profile_pic;
        this.nickname = nickname;
        this.username = username;
        this.event_date = event_date;
        this.zone = zone;
        this.context = context;
        this.num_players = num_players;
        this.num_added = num_added;
        this.gamename = gamename;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getNumPosts() {
        return numposts;
    }

    public String getDate() {
        return date;
    }

    public String getCat() {
        return cat;
    }

    public String getTopic_by() {
        return topic_by;
    }

    public String getType() { return type; }

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

    public String getEvent_date() {
        return event_date;
    }

    public String getZone() {
        return zone;
    }

    public String getContext() {
        return context;
    }

    public String getNum_players() {
        return num_players;
    }

    public String getNum_added() {
        return num_added;
    }

    public String getGamename() {
        return gamename;
    }


   /* public static List<Publics_Recycler> createPublics(int itemCount) {
        List<Publics_Recycler> npPublics = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Publics_Recycler newPublics = new Publics_Recycler(itemCount  + (itemCount == 0 ?
                    (itemCount + 1 + i) : (itemCount + i)), "yeeetskeet", "yelllooo", "dang.jpg");


            npPublics.add(newPublics);
        }
        return npPublics;
    }*/
}
