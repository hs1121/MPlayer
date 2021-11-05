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

    @Update
    suspend fun update(entity: PlaylistEntity)

    @Query("SELECT * FROM playlist")
     fun getAll(): Flow<MutableList<PlaylistEntity>?>

}