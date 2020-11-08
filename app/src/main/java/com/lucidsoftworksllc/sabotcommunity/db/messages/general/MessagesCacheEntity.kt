package com.lucidsoftworksllc.sabotcommunity.db.messages.general

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "messages_general")
data class MessagesCacheEntity (

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Int,

        @ColumnInfo(name = "loaded")
        val loaded: Boolean,

        @ColumnInfo(name = "sent_by")
        val sent_by: String,

        @ColumnInfo(name = "body_split")
        val body_split: String,

        @ColumnInfo(name = "time_message")
        val time_message: String,

        @ColumnInfo(name = "latest_profile_pic")
        val latest_profile_pic: String,

        @ColumnInfo(name = "profile_pic")
        val profile_pic: String,

        @ColumnInfo(name = "nickname")
        val nickname: String,

        @ColumnInfo(name = "verified")
        val verified: String,

        @ColumnInfo(name = "last_online")
        val last_online: String,

        // TODO should be INT with new backend API
        @ColumnInfo(name = "viewed")
        val viewed: String,

        @ColumnInfo(name = "message_id")
        val message_id: Int,

        @ColumnInfo(name = "user_from")
        val user_from: String,

        @ColumnInfo(name = "type")
        val type: String,

        @ColumnInfo(name = "group_id")
        val group_id: Int

)