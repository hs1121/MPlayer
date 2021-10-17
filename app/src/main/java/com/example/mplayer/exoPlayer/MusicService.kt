package com.example.mplayer.exoPlayer

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media.MediaBrowserServiceCompat
import com.example.mplayer.Constants.MY_MEDIA_ROOT_ID
import com.example.mplayer.Constants.ROOT_PACKAGE
import com.example.mplayer.MainActivity
import com.example.mplayer.PlayerActivity
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject

const val SERVICE_TAG="MusicService"

@AndroidEntryPoint
class MusicService: MediaBrowserServiceCompat() {


    var isForegroundService: Boolean=false
    var currentlyPlayingSong:MediaMetadataCompat?=null
    var activityIntent:PendingIntent?=null
    private lateinit var playerNotificationManager:PlayerNotificationManager

    companion object{
        private val _playerInstance = MutableLiveData<SimpleExoPlayer?>()
        val playerInstance:LiveData<SimpleExoPlayer?> = _playerInstance
    }

    @Inject
     lateinit var exoPlayer: SimpleExoPlayer
     @Inject
     lateinit var musicSource: MusicSource
     @Inject
     lateinit var dataSourceFactory: DefaultDataSourceFactory
     lateinit var mediaSession : MediaSessionCompat
     private lateinit var playerEventListener:PlayerListener



    private val serviceJob= Job()
    private val serviceScope= CoroutineScope(Dispatchers.Main+serviceJob)


    override fun onCreate() {
        super.onCreate()

        val intentParent=Intent(this,MainActivity::class.java)
        intentParent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
        val intent = Intent(this, PlayerActivity::class.java)
        val intents = arrayOf(intentParent, intent)
        val pendingIntent = PendingIntent.getActivities(this,0, intents, PendingIntent.FLAG_UPDATE_CURRENT)
        activityIntent=pendingIntent


        mediaSession= MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive=true
        }
        sessionToken=mediaSession.sessionToken
        val playbackPreparer=PlayerPlayBackPreparer(musicSource){
            currentlyPlayingSong=it
            mediaSession.setMetadata(it)

            preparePlayer(
                musicSource.musicItems,
                it,
                true
            )
        }


        val mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(playbackPreparer)
        mediaSessionConnector.setPlayer(exoPlayer)
         mediaSessionConnector.setQueueNavigator(PlayerQueueNavigator(mediaSession))
        playerEventListener=PlayerListener(this)
        exoPlayer.addListener(playerEventListener)
         playerNotificationManager=PlayerNotificationManager(this).apply {
            getNotificationManager().setPlayer(exoPlayer)
        }


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
        _playerInstance.postValue(exoPlayer)
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
    private inner class PlayerQueueNavigator(mediaSessionCompat: MediaSessionCompat):TimelineQueueNavigator(mediaSessionCompat){
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return musicSource.musicItems[windowIndex].description
        }

    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()

    }

    override fun onDestroy() {
        serviceScope.cancel()

        _playerInstance.postValue(null)
        exoPlayer.removeListener(playerEventListener)
        exoPlayer.release()
        playerNotificationManager.getNotificationManager().setPlayer(null)
        super.onDestroy()

    }


}