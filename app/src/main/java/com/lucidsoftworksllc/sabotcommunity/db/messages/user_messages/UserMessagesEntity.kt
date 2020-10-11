package com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UserMessagesEntity (
    @PrimaryKey(autoGenerate = false) var message_id: Int,
    val user_to: String,
    val user_from: String,
    val body: String,
    val date: String,
    val image: String
)