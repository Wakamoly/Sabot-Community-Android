package com.lucidsoftworksllc.sabotcommunity.models

data class CurrentPublicsPOJO (
    var id: String,
    var subject: String,
    var catname: String,
    var type: String,
    var profilePic: String,
    var nickname: String,
    var eventDate: String,
    var context: String,
    var numPlayers: String,
    var playingNow1: String,
    var numAdded: String,
    var image: String,
    var playingNow: String
)

data class CurrentPublicsModel_autogen(
        val catname: String,
        val context: String,
        val event_date: String,
        val id: Int,
        val image: String,
        val nickname: String,
        val num_added: Int,
        val num_players: Int,
        val playing_now: String,
        val profile_pic: String,
        val subject: String,
        val type: String
)