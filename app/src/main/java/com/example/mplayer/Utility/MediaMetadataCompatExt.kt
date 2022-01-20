package com.example.mplayer.Utility

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.example.mplayer.Constants.METADATA_KEY_DATE
import com.example.mplayer.Constants.METADATA_KEY_FLAG
import com.example.mplayer.Constants.METADATA_KEY_FROM
import com.example.mplayer.Constants.NO_GET


inline var MediaMetadataCompat.Builder.flag: Int
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(METADATA_KEY_FLAG, value.toLong())
    }

inline var MediaMetadataCompat.Builder.from: String
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
          putString(METADATA_KEY_FROM,value)
    }

inline var MediaMetadataCompat.Builder.date: Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(METADATA_KEY_DATE,value)
    }




inline var MediaMetadataCompat.from: String
    set(value) {
         bundle.putString(METADATA_KEY_FROM,value)
    }

    get() {
        val key1=getText(METADATA_KEY_FROM)?:""

        return key1.toString()
    }

inline var MediaMetadataCompat.date: Long
    set(value) {
       bundle.putLong(METADATA_KEY_DATE,value)}
    get() {
        return getLong(METADATA_KEY_DATE)
    }


inline var MediaDescriptionCompat.Builder.from : String
set(value) {setExtras(Bundle().apply { putString(METADATA_KEY_FROM,value) })
}
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")

inline var MediaDescriptionCompat.Builder.date : Long
    set(value) {setExtras(Bundle().apply { putLong(METADATA_KEY_DATE,value) })}
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")


inline var MediaDescriptionCompat.from : String
    set(value) {
        extras?.putString(METADATA_KEY_FROM,value)
    }
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")

inline var MediaDescriptionCompat.date:Long
    set(value) { extras?.putLong(METADATA_KEY_DATE,value) }
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")




inline var MediaBrowserCompat.MediaItem.from : String
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    set(value) = throw IllegalAccessException("Cannot set  MediaDescriptionCompat")
    get() {
       val key= description.extras?.getString(METADATA_KEY_FROM).toString()
        return key
    }

inline var MediaBrowserCompat.MediaItem.date : Long
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    set(value) = throw IllegalAccessException("Cannot set  MediaDescriptionCompat")
    get() {
        return description.extras?.getLong(METADATA_KEY_DATE)?:0
    }

