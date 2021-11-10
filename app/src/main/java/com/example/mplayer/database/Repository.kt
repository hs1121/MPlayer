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
    suspend fun deletePlaylist(name: MutableList<String>){
        name.forEach {
            playerDatabase.playlistDao().delete(it)
        }

    }


    suspend fun updatePlaylist(entity: PlaylistEntity){
        playerDatabase.playlistDao().update(entity)
    }


     fun getAllPlaylists(): Flow<MutableList<PlaylistEntity>?>{
        return playerDatabase.playlistDao().getAll()
     }

   suspend fun updatePlaylistITem(item:MutableList<String?>,name:String){
       val entity= playerDatabase.playlistDao().getPlaylistItem(name)
       entity.data=item
        updatePlaylist(entity)
    }

}