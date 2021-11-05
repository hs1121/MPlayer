package com.example.mplayer.database

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class TypeConvertor {

    val gson = Gson()

    @TypeConverter
    fun mediaMetadataToString(list: MutableList<MediaBrowserCompat.MediaItem>):String{
        return gson.toJson(list)
    }
    @TypeConverter
    fun stringToMediaMetadata(string: String):MutableList<MediaBrowserCompat.MediaItem>{
        val listType= object : TypeToken<MutableList<MediaBrowserCompat.MediaItem>>() {}.type

        return try {
            gson.fromJson(string, listType)
        } catch (e:Exception){
            Log.e("TypeConvert",e.message.toString())
            mutableListOf()
        }
    }

    @TypeConverter
    fun listToString(list: MutableList<String?>):String{
        return gson.toJson(list)
    }
    @TypeConverter
    fun stringToList(string: String):MutableList<String?>{
        val listType= object : TypeToken<MutableList<String?>>() {}.type

        return try {
            gson.fromJson(string, listType)
        } catch (e:Exception){
            Log.e("TypeConvert",e.message.toString())
            mutableListOf()
        }
    }

}