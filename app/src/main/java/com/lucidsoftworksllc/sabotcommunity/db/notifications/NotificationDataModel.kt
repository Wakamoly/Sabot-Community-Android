package com.lucidsoftworksllc.sabotcommunity.db.notifications

data class NotificationDataModel (
        var id: Int,
        var user_to: String,
        var user_from: String,
        var message: String,
        var link: String,
        var datetime: String,
        var opened: String,
        var viewed: String,
        val user_id: String,
        val profile_pic: String,
        val nickname: String,
        val verified: String,
        val last_online: String,
        val type: String,
        val deleted: String
)