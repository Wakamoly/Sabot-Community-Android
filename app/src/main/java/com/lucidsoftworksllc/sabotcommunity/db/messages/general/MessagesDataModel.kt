package com.lucidsoftworksllc.sabotcommunity.db.messages.general

data class MessagesDataModel(
        var id: Int,
        val loaded: Boolean,
        val sent_by: String,
        val body_split: String,
        val time_message: String,
        val latest_profile_pic: String,
        val profile_pic: String,
        val nickname: String,
        val verified: String,
        val last_online: String,
        val viewed: String,
        val message_id: Int,
        val user_from: String,
        val type: String,
        val group_id: Int)