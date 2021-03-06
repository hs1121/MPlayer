package com.example.mplayer.exoPlayer

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.example.mplayer.Constants
import com.example.mplayer.Constants.TRACKS_ROOT
import com.example.mplayer.database.BrowsingTree
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector

class PlayerPlayBackPreparer(
    val browsingTree: BrowsingTree,
    val playerPrepared : (MediaMetadataCompat?,Long,Boolean) -> Unit
):MediaSessionConnector.PlaybackPreparer {
    override fun onCommand(
        player: Player,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    )=false

    override fun getSupportedPrepareActions(): Long { // sets flags by which means(functions) the player should be executed
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }

    override fun onPrepare(playWhenReady: Boolean) =Unit

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
       browsingTree.whenReady {
           var key= extras?.get(Constants.METADATA_KEY_FROM).toString()
           // the key is required to play specific playlist or album so that concatenating mediaSource
           // of correct list can be used
           if(key.isEmpty())
               key=TRACKS_ROOT
            val media=browsingTree.browsingList[key]?.find { it.description.mediaId==mediaId }
           val seekTo=extras?.getLong(Constants.SEEK_TO,0L)?:0L
           val playMedia=extras?.getBoolean(Constants.PLAY_MEDIA,true)?:true
           playerPrepared(media,seekTo,playMedia)
       }
    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?)=Unit

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?)= Unit
}