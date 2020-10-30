package com.lucidsoftworksllc.sabotcommunity.models.network_autogen

data class SentMessageResponse(
    val error: Boolean,
    val messageid: Int
)

data class SentImageResponse(
        val error: Boolean,
        val imagepath: String,
        val message: String
)