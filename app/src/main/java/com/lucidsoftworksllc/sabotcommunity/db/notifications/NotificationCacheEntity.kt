package com.lucidsoftworksllc.sabotcommunity.db.notifications

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationCacheEntity (

        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "id")
        var id: Int,

        @ColumnInfo(name = "user_to")
        var user_to: String,

        @ColumnInfo(name = "user_from")
        var user_from: String,

        @ColumnInfo(name = "message")
        var message: String,

        @ColumnInfo(name = "type")
        var type: String,

        @ColumnInfo(name = "link")
        var link: String,

        @ColumnInfo(name = "datetime")
        var datetime: String,

        @ColumnInfo(name = "opened")
        var opened: String,

        @ColumnInfo(name = "viewed")
        var viewed: String,

        @ColumnInfo(name = "user_id")
        val user_id: String,

        @ColumnInfo(name = "profile_pic")
        val profile_pic: String,

        @ColumnInfo(name = "nickname")
        val nickname: String,

        @ColumnInfo(name = "verified")
        val verified: String,

        val last_online: String = "no",

        @ColumnInfo(name = "deleted")
        val deleted: String

)