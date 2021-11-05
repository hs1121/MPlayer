package com.example.mplayer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mplayer.database.dao.PlaylistDao
import com.example.mplayer.database.entity.PlaylistEntity

@Database(entities = [PlaylistEntity::class],version = 1,exportSchema = false)
@TypeConverters(TypeConvertor::class)
abstract class MPlayerDatabase:RoomDatabase() {


    abstract fun playlistDao() : PlaylistDao

}