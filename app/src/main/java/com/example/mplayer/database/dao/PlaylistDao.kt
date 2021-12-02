package com.example.mplayer.database.dao

import androidx.room.*
import com.example.mplayer.database.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity:PlaylistEntity)

    @Delete
    suspend fun delete(entity: PlaylistEntity)


    @Update()
    suspend fun update(entity: PlaylistEntity)

    @Query("SELECT * FROM playlist")
     fun getAll(): Flow<MutableList<PlaylistEntity>?>

    @Query("SELECT * FROM playlist")
    suspend fun getPlaylists(): MutableList<PlaylistEntity>?

    @Query("SELECT * FROM Playlist WHERE NAME=:name")
    suspend fun getPlaylistItem(name:String):PlaylistEntity


    @Query("DELETE FROM Playlist WHERE NAME=:name")
    suspend fun delete(name:String)

}