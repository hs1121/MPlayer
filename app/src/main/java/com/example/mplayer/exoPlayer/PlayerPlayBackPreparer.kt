package com.example.mplayer.exoPlayer

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.provider.SyncStateContract
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.example.mplayer.Constants
import com.example.mplayer.Constants.TRACKS_ROOT
import com.example.mplayer.Utility.BrowsingTree
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector

class PlayerPlayBackPreparer(
    val browsingTree: BrowsingTree,
    val playerPrepared : (MediaMetadataCompat?) -> Unit
):MediaSessionConnector.PlaybackPreparer {
    override fun onCommand(
        player: Player,
        controlDispatcher: ControlDispatcher,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    )=false

    override fun getSupportedPrepareActions(): Long {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }

    override fun onPrepare(playWhenReady: Boolean) =Unit

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
       browsingTree.whenReady {
           val key= extras?.get(Constants.METADATA_KEY_FROM)
            val media=browsingTree.browsingList[key]?.find { it.description.mediaId==mediaId }
           playerPrepared(media)
       }
    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?)=Unit

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?)= Unit
}