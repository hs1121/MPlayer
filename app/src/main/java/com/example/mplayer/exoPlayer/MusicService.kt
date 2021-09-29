package com.example.mplayer.exoPlayer

import android.app.PendingIntent
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.example.mplayer.Constants.MY_MEDIA_ROOT_ID
import com.example.mplayer.Constants.ROOT_PACKAGE
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject

const val SERVICE_TAG="MusicService"

@AndroidEntryPoint
class MusicService: MediaBrowserServiceCompat() {


    var isForegroundService: Boolean=false
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    var currentlyPlayingSong:MediaMetadataCompat?=null

    @Inject
     lateinit var exoPlayer: SimpleExoPlayer
     @Inject
     lateinit var musicSource: MusicSource
     @Inject
     lateinit var dataSourceFactory: DefaultDataSourceFactory
    private lateinit var mediaSession : MediaSessionCompat
 //   private lateinit var sessionConnector: MediaSessionConnector

    private val serviceJob= Job()
    private val serviceScope= CoroutineScope(Dispatchers.Main+serviceJob)


    override fun onCreate() {
        super.onCreate()

        val activityIntent=packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        mediaSession= MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive=true
            setFlags(FLAG_HANDLES_MEDIA_BUTTONS)
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())


        }
            sessionToken=mediaSession.sessionToken
        val playbackPreparer=PlayerPlayBackPreparer(musicSource){
            currentlyPlayingSong=it
            preparePlayer(
                musicSource.musicItems,
                it,
                true
            )
        }

        val mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(playbackPreparer)
        mediaSessionConnector.setPlayer(exoPlayer)

        exoPlayer.addListener(PlayerListener(this))


    }

    private fun preparePlayer(
    songs:MutableList<MediaMetadataCompat>,
    currentSong:MediaMetadataCompat?,
    playNow:Boolean
    ){
        val currentIndex= if (currentlyPlayingSong==null) 0 else songs.indexOf(currentSong)
        exoPlayer.setMediaSource(musicSource.asMusicSource(dataSourceFactory))
        exoPlayer.prepare()
        exoPlayer.seekTo(currentIndex,0L)
        exoPlayer.playWhenReady=playNow
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val resultsSent = musicSource.whenReady { successfullyInitialized ->
            if (successfullyInitialized) {
                val children: MutableList<MediaBrowserCompat.MediaItem> = when(parentId) {
                    ROOT_PACKAGE-> musicSource.musicItems.map { item ->
                        MediaBrowserCompat.MediaItem(item.description, FLAG_PLAYABLE)
                    } as MutableList<MediaBrowserCompat.MediaItem>
                    else -> emptyList<MediaBrowserCompat.MediaItem>() as MutableList<MediaBrowserCompat.MediaItem>
                }
                            result.sendResult(children)
                }
             else {
                result.sendResult(null)
            }
        }
        if (!resultsSent) {
            result.detach()
        }
    }

}