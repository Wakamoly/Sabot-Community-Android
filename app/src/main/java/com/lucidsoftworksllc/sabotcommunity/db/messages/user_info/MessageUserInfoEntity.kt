package com.lucidsoftworksllc.sabotcommunity.db.messages.user_info

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages_user_info")
data class MessageUserInfoEntity (
        @PrimaryKey(autoGenerate = false) var user_id: Int,
        var profile_pic: String,
        var nickname: String,
        var verified: String,
        var last_online: String,
        var blocked_array: String,
        var username: String
)