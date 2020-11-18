package com.lucidsoftworksllc.sabotcommunity.db.messages.typed

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages_typed")
data class TypedMessageEntity (
        val type: String,
        val username: String,
        val group_id: Int,
        val message: String
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}