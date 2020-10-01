package com.lucidsoftworksllc.sabotcommunity.db.notifications

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NotificationNetworkEntity (

        @SerializedName("id")
        @Expose var id: Int,

        @SerializedName("user_to")
        @Expose var user_to: String,

        @SerializedName("user_from")
        @Expose var user_from: String,

        @SerializedName("message")
        @Expose var message: String,

        @SerializedName("link")
        @Expose var link: String,

        @SerializedName("datetime")
        @Expose var datetime: String,

        @SerializedName("opened")
        @Expose var opened: String,

        @SerializedName("viewed")
        @Expose var viewed: String,

        @SerializedName("user_id")
        @Expose val user_id: String,

        @SerializedName("profile_pic")
        @Expose val profile_pic: String,

        @SerializedName("nickname")
        @Expose val nickname: String,

        @SerializedName("verified")
        @Expose val verified: String,

        @SerializedName("last_online")
        @Expose val last_online: String,

        @SerializedName("type")
        @Expose val type: String

)