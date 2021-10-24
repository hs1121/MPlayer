package com.example.mplayer.Utility

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.example.mplayer.Constants.METADATA_KEY_FLAG
import com.example.mplayer.Constants.NO_GET

@MediaBrowserCompat.MediaItem.Flags
inline var MediaMetadataCompat.Builder.flag: Int
    @Deprecated(NO_GET, level = DeprecationLevel.ERROR)
    get() = throw IllegalAccessException("Cannot get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(METADATA_KEY_FLAG, value.toLong())
    }