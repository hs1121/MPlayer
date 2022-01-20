package com.example.mplayer

object Constants {
        const val PERMISSION_READ_INTERNAL_STORAGE=10
        const val PERMISSION_WRITE_EXTERNAL_STORAGE=11

        const val REQUEST_CODE_DELETE=5
        const val SEEK_TO="SeekToPosition"
        const val PLAY_MEDIA="PlayMedia"
        const val NOTIFICATION_ID=101
        const val CHANNEL_ID="playback_notification_channel_id"

        const val CHANNEL_NAME="Playback Notification Channel"

        const val MY_MEDIA_ROOT_ID="media_root"

        const val TRACKS_ROOT="_TRACKS_"
        const val ALBUMS_ROOT="_ALBUMS_"
        const val PLAYLIST_ROOT="_PLAYLISTS_"
        
        const val METADATA_KEY_FLAG="com.example.mplayer.Utility.METADATA_KEY_FLAG"
        const val METADATA_KEY_FROM="com.example.mplayer.Utility.METADATA_KEY_FROM"
        const val METADATA_KEY_DATE="com.example.mplayer.Utility.METADATA_KEY_DATE"

        const val NO_GET = "Property does not have a 'get'"
        const val FROM_SPLASH_SCREEN="fromSplashScreen"

        const val PLAYER_DATABASE="MPlayerDatabase"
        const val PLAYLIST_TABLE="Playlist"
}