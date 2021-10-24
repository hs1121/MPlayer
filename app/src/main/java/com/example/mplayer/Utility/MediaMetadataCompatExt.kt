package com.example.mplayer.Utility

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.example.mplayer.Constants.METADATA_KEY_FLAG
import com.example.mplayer.Constants.METADATA_KEY_FROM
import com.example.mplayer.Constants.NO_GET
import com.example.mplayer.Constants.PLAYLIST_ROOT

@MediaBrowserCompat.MediaItem.Flags
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


inline var MediaMetadataCompat.from: String
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    set(value) = throw IllegalAccessException("Cannot set  MediaMetadataCompat")
    get() {
        val key1=getText(METADATA_KEY_FROM).toString()

        return key1
    }

inline var MediaDescriptionCompat.Builder.from : String
set(value) {setExtras(Bundle().apply { putString(METADATA_KEY_FROM,value) })}
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")

inline var MediaBrowserCompat.MediaItem.from : String
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    set(value) = throw IllegalAccessException("Cannot set  MediaDescriptionCompat")
    get() {
       val key= description.extras?.getString(METADATA_KEY_FROM).toString()
        return key
    }

