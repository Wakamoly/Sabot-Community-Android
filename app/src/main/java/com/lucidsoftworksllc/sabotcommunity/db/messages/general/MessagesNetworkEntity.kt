package com.lucidsoftworksllc.sabotcommunity.db.messages.general

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MessagesNetworkEntity (

    val id: Int = 0,
    val loaded: Boolean = false,

    @SerializedName("sent_by")
    @Expose val sent_by: String,

    @SerializedName("body_split")
    @Expose val body_split: String,

    @SerializedName("time_message")
    @Expose val time_message: String,

    @SerializedName("latest_profile_pic")
    @Expose val latest_profile_pic: String,

    @SerializedName("profile_pic")
    @Expose val profile_pic: String,

    @SerializedName("nickname")
    @Expose val nickname: String,

    @SerializedName("verified")
    @Expose val verified: String,

    @SerializedName("last_online")
    @Expose val last_online: String,

    @SerializedName("viewed")
    @Expose val viewed: String,

    @SerializedName("id")
    @Expose val message_id: Int,

    @SerializedName("user_from")
    @Expose val user_from: String,

    @SerializedName("type")
    @Expose val type: String,

    @SerializedName("group_id")
    @Expose val group_id: Int

)