package com.lucidsoftworksllc.sabotcommunity;

class Clans_Recycler {
    private String position,tag,name,num_members,insignia,games,id,avg;

    public Clans_Recycler(String position, String tag, String name, String num_members, String insignia, String games, String id, String avg) {
        this.position = position;
        this.tag = tag;
        this.name = name;
        this.num_members = num_members;
        this.insignia = insignia;
        this.games = games;
        this.id = id;
        this.avg = avg;
    }


    public String getPosition() {
        return position;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public String getNum_members() {
        return num_members;
    }

    public String getInsignia() {
        return insignia;
    }

    public String getGames() {
        return games;
    }

    public String getId() {
        return id;
    }

    public String getAvg() {
        return avg;
    }
}
