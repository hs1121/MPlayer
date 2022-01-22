package com.example.mplayer.exoPlayer

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media.MediaBrowserServiceCompat
import com.example.mplayer.Constants.MY_MEDIA_ROOT_ID
import com.example.mplayer.Constants.NOTIFICATION_ID
import com.example.mplayer.MainActivity
import com.example.mplayer.PlayerActivity
import com.example.mplayer.Utility.PreferenceDataStore
import com.example.mplayer.database.BrowsingTree
import com.example.mplayer.Utility.from
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

const val SERVICE_TAG = "MusicService"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {


    var isForegroundService: Boolean = false
    var currentlyPlayingSong: MediaMetadataCompat? = null
    var activityIntent: PendingIntent? = null
    private lateinit var playerNotificationManager: PlayerNotificationManager

    companion object {

         var equalizer: Equalizer?=null
         var bass:BassBoost?=null
         var presetReverb: PresetReverb?=null




        // player instance (used to connect exoplayer ui to player)
        private val _playerInstance = MutableLiveData<ExoPlayer?>()
        val playerInstance: LiveData<ExoPlayer?> = _playerInstance

        var currentSong: MediaMetadataCompat? = null
    }

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var browsingTree: BrowsingTree
    @Inject
    lateinit var preferenceDataStore: PreferenceDataStore

    @Inject
    lateinit var dataSourceFactory: DefaultDataSource.Factory
    lateinit var mediaSession: MediaSessionCompat
    private lateinit var playerEventListener: PlayerListener


    // not used (can be used to get network calls etc).
    private val serviceJob = Job()
     val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private lateinit var  mediaSessionConnector : MediaSessionConnector


    override fun onCreate() {
        super.onCreate()
        val intentParent = Intent(this, MainActivity::class.java)
        intentParent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
        val intent = Intent(this, PlayerActivity::class.java)
        val intents = arrayOf(intentParent, intent)   // makes mainActivity open first and then player activity for better UX

        // pending intent for player activity
        val pendingIntent :PendingIntent = PendingIntent.getActivities(this, 0, intents,
                 PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)


        activityIntent = pendingIntent


        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken
         mediaSessionConnector = MediaSessionConnector(mediaSession)

        val playbackPreparer = PlayerPlayBackPreparer(browsingTree) {it,seetTo,playMedia-> // executes whenever user changes the media(callback method)
            currentlyPlayingSong = it
            currentSong=it
            mediaSession.setMetadata(it)
            val key = it?.from ?: ""
            mediaSessionConnector.setQueueNavigator(PlayerQueueNavigator(mediaSession, key)) // sets the

            preparePlayer(
                browsingTree.mediaListByItem(it),
                it,
                key,
                playMedia,
                seetTo
            )

        }
        mediaSessionConnector.setPlaybackPreparer(playbackPreparer)
        mediaSessionConnector.setPlayer(exoPlayer)
        playerNotificationManager = PlayerNotificationManager(this).apply {
            getNotificationManager().setPlayer(exoPlayer)
        }
        playerEventListener = PlayerListener(this,playerNotificationManager,preferenceDataStore)
        exoPlayer.addListener(playerEventListener)
        _playerInstance.postValue(exoPlayer)


    }


    private fun preparePlayer(
        songs: MutableList<MediaMetadataCompat>,
        currentSong: MediaMetadataCompat?,
        from: String?,
        playNow: Boolean,
        seekTo:Long=0L
    ) {
        val currentIndex = if (currentlyPlayingSong == null) 0 else songs.indexOf(currentSong)
        exoPlayer.setMediaSource(browsingTree.asMusicSource(dataSourceFactory, from)) // set concatenating media source
        exoPlayer.prepare()
        exoPlayer.seekTo(currentIndex, seekTo)
        exoPlayer.playWhenReady = playNow

    }

    override fun onGetRoot(
        // returns root id (can decide that which key to be passed using clintPackageName)
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)


    }

    override fun onLoadChildren(// sends media which is requested by key from browsing tree
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val resultsSent = browsingTree.whenReady { successfullyInitialized ->
            if (successfullyInitialized) {
                val children: MutableList<MediaBrowserCompat.MediaItem> =
                    browsingTree.asMediaItem(parentId) ?: mutableListOf()
                result.sendResult(children)
            } else {
                result.sendResult(null)
            }
        }
        if (!resultsSent) {
            result.detach()
        }
    }

    private inner class PlayerQueueNavigator(
        // sync the playlist (concatenating media source) with timeline
        // ie. controls seek change to next song (updates state and metadata
        mediaSessionCompat: MediaSessionCompat,
        private val keyString: String
    ) : TimelineQueueNavigator(mediaSessionCompat) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return browsingTree.musicItem(keyString)?.get(windowIndex)?.description!!
        }

    }
    fun setUpEqualizer(){
         var eq:Equalizer
         val sessionId=exoPlayer.audioSessionId
         eq=Equalizer(1000,sessionId)

    }




    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
        Log.i("Notification","onTaskRemoved")

    }


    override fun onDestroy() {
        serviceScope.cancel()
        equalizer?.release()
        bass?.release()
        presetReverb?.release()
        playerNotificationManager.getNotificationManager().setPlayer(null)
        mediaSession.isActive = false
        mediaSession.release()
        mediaSessionConnector.setPlayer(null)
        _playerInstance.postValue(null)
        exoPlayer.removeListener(playerEventListener)
        exoPlayer.release()
        Log.i("Notification","onDestroy")
        super.onDestroy()

    }



}