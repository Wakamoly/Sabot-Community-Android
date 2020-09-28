package com.lucidsoftworksllc.sabotcommunity.retrofit

import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("type")
    val type: String? = null

    @SerializedName("id")
    val id: String? = null

    @SerializedName("name")
    val name: String? = null

    @SerializedName("subname")
    val subname: String? = null

    @SerializedName("image")
    val image: String? = null

    @SerializedName("extra")
    val extra: String? = null

    @SerializedName("verified")
    val verified: String? = null

    @SerializedName("last_online")
    val last_Online: String? = null

    @SerializedName("numratings")
    val numratings: String? = null
}