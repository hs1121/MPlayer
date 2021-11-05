package com.example.mplayer.database

import androidx.room.*
import com.example.mplayer.database.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(
    val browsingTree: BrowsingTree,
    val playerDatabase: MPlayerDatabase
){

    suspend fun insertPlaylist(entity: PlaylistEntity){
        playerDatabase.playlistDao().insert(entity)
    }


    suspend fun deletePlaylist(entity: PlaylistEntity){
        playerDatabase.playlistDao().delete(entity)
    }


    suspend fun updatePlaylist(entity: PlaylistEntity){
        playerDatabase.playlistDao().update(entity)
    }


     fun getAllPlaylists(): Flow<MutableList<PlaylistEntity>?>{
        return playerDatabase.playlistDao().getAll()
     }

}