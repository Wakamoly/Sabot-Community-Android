package com.lucidsoftworksllc.sabotcommunity.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PublicsEntity (
    @PrimaryKey(autoGenerate = false) var id: Int,
    var title: String,
    var genre: String,
    var image: String,
    var numratings: Int,
    var avgrating: String,
    var tag: String,
    var postcount: Int,
    var followed: String,
    var followers: Int,
    var platforms: String
)