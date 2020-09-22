package com.lucidsoftworksllc.sabotcommunity.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PublicsEntity (
    @PrimaryKey(autoGenerate = false) var id: String,
    var title: String,
    var genre: String,
    var image: String,
    var numratings: String,
    var avgrating: String,
    var tag: String,
    var postcount: String,
    var followed: String,
    var followers: Int
)