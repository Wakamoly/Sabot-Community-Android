package com.lucidsoftworksllc.sabotcommunity.models


//TODO convert in backend to objects
data class UsersOnlineModel (
        val usersonline: UsersOnline,
        val dashnews: DashNews
)

data class DashNews (
        val bottomtext: String,
        val link: String,
        val toptext: String
)

data class UsersOnline (
        val num: Int,
        val numpublics: Int,
        val unreadmessages: Int
)
