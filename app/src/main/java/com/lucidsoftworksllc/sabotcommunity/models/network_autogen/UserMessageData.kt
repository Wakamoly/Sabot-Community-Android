package com.lucidsoftworksllc.sabotcommunity.models.network_autogen

import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesEntity

data class UserMessageData(
    val error: Boolean,
    val messages: List<UserMessagesEntity>
)

data class UserMessagesFromID(
        val error: Boolean,
        val messages: List<UserMessagesEntity>,
        val userLastOnline: String
)