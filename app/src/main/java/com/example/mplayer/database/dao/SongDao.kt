package com.example.mplayer.database.dao

import androidx.room.*
import com.example.mplayer.database.entity.SongEntity

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity:SongEntity)

    @Delete
    fun delete(entity: SongEntity)

    @Update
    fun update(entity: SongEntity)
}