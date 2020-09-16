package com.lucidsoftworksllc.sabotcommunity;

import java.util.ArrayList;
import java.util.List;

public class PublicsPost_Recycler {

    private String id;
    private String subject;
    private String date;
    private String cat;
    private String topic_by;
    private String post_id;
    private String post_topic;
    private String post_content;
    private String post_date;
    private String post_by;
    private String user_id;
    private String profile_pic;
    private String nickname;
    private String username;
    private String voted;
    private String votes;
    private String replies;

    private String online;
    private String verified;
    private String clantag;

    public PublicsPost_Recycler(String id, String subject, String date, String cat, String topic_by, String post_id, String post_topic, String post_content, String post_date, String post_by, String user_id, String profile_pic, String nickname, String username, String voted, String votes, String replies, String online, String verified, String clantag) {
        this.id = id;
        this.subject = subject;
        this.date = date;
        this.cat = cat;
        this.topic_by = topic_by;
        this.post_id = post_id;
        this.post_topic = post_topic;
        this.post_content = post_content;
        this.post_date = post_date;
        this.post_by = post_by;
        this.user_id = user_id;
        this.profile_pic = profile_pic;
        this.nickname = nickname;
        this.username = username;
        this.voted = voted;
        this.votes = votes;
        this.replies = replies;
        this.online = online;
        this.verified = verified;
        this.clantag = clantag;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
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

    public String getPost_id() {
        return post_id;
    }

    public String getPost_topic() {
        return post_topic;
    }

    public String getPost_content() {
        return post_content;
    }

    public String getPost_date() {
        return post_date;
    }

    public String getPost_by() {
        return post_by;
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

    public String getVoted() {
        return voted;
    }

    public String getVotes() {
        return votes;
    }

    public String getReplies() {
        return replies;
    }

    public String getOnline() {
        return online;
    }

    public String getVerified() {
        return verified;
    }

    public String getClantag() {
        return clantag;
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
