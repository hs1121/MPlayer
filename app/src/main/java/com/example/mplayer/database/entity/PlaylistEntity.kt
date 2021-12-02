package com.example.mplayer.database.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mplayer.Constants.PLAYLIST_TABLE

@Entity(tableName = PLAYLIST_TABLE)
data class PlaylistEntity(
    var name: String = "",
    var createdBy: String = "unknown",
    var createdOn: String = "",
    var iconUri: String? = null,
    var data: MutableList<String?> = mutableListOf(),
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)
