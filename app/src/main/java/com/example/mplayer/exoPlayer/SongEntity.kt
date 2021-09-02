package com.example.mplayer.exoPlayer

import androidx.room.PrimaryKey

data class SongEntity(
    var name:String?="",

    var path:String?="",
    var artist:String?="",
    var album:String?="",
    var albumArt:String?="",
    var dateAdded:Long?=0,
    var dateModified:Long?=0,
    @PrimaryKey(autoGenerate = false)
    var pathUri:String="",
    var duration:String?=""
)