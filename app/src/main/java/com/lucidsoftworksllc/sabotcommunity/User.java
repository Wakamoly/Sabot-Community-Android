package com.lucidsoftworksllc.sabotcommunity;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("type") private String Type;
    @SerializedName("id") private String Id;
    @SerializedName("name") private String Name;
    @SerializedName("subname") private String Subname;
    @SerializedName("image") private String Image;
    @SerializedName("extra") private String Extra;
    @SerializedName("verified") private String Verified;
    @SerializedName("last_online") private String Last_Online;
    @SerializedName("numratings") private String Numratings;

    public String getType() { return Type; }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getSubname() {
        return Subname;
    }

    public String getImage() {
        return Image;
    }

    public String getExtra() {
        return Extra;
    }

    public String getVerified() {
        return Verified;
    }

    public String getLast_Online() {
        return Last_Online;
    }

    public String getNumratings() {
        return Numratings;
    }
}