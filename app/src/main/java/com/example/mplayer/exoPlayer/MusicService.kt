package com.example.mplayer.exoPlayer

import android.app.PendingIntent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject

const val SERVICE_TAG="MusicService"

@AndroidEntryPoint
class MusicService: MediaBrowserServiceCompat() {

    @Inject
     lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var sessionConnector: MediaSessionConnector

    private val serviceJob= Job()
    private val serviceScope= CoroutineScope(Dispatchers.Main+serviceJob)


    override fun onCreate() {
        super.onCreate()

        val activityIntent=packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        mediaSession= MediaSessionCompat.fromMediaSession(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive=true
        }
        sessionToken=mediaSession.sessionToken

    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }
}