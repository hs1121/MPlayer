package com.example.mplayer.exoPlayer

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media.MediaBrowserServiceCompat
import com.example.mplayer.Utility.from
import com.google.android.exoplayer2.SimpleExoPlayer
import javax.inject.Inject

class MediaSessionConnection(
    private val context: Context,
    serviceComponent: ComponentName) {

    private val _isConnected=MutableLiveData<Boolean>()
     val isConnected=_isConnected as LiveData<Boolean>

    private val _playbackState=MutableLiveData<PlaybackStateCompat?>()
     val playbackState=_playbackState as LiveData<PlaybackStateCompat?>

    private val _nowPlaying=MutableLiveData<MediaMetadataCompat>()
     val nowPlaying=_nowPlaying as LiveData<MediaMetadataCompat>


    val transportControls:MediaControllerCompat.TransportControls
    get ()= mediaControllerCompat.transportControls

    private lateinit var mediaControllerCompat: MediaControllerCompat
    private val connectionCallback=ConnectionCallback()
    private val mediaBrowser=MediaBrowserCompat(
        context,
        serviceComponent,
        connectionCallback,
        null
    ).apply { connect() }


    fun subscribe(parentId:String, subscriptionCallback: MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.subscribe(parentId,subscriptionCallback)
    }

    fun unSubscribe(parentId:String, subscriptionCallback:MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.unsubscribe(parentId,subscriptionCallback)
    }



    private inner class ConnectionCallback: MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            _isConnected.postValue(true)
            mediaControllerCompat= MediaControllerCompat(context,mediaBrowser.sessionToken).apply {
                registerCallback(ControllerCallback())
            }
        //    MediaControllerCompat.setMediaController(context as Activity,mediaControllerCompat)


        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            _isConnected.postValue(false)
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            _isConnected.postValue(false)
        }
    }

    private inner class ControllerCallback:MediaControllerCompat.Callback(){
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            _playbackState.postValue(state?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
//           _nowPlaying.postValue( MusicService.currentSong?:NOTHING_PLAYING)
            _nowPlaying.postValue(metadata?:NOTHING_PLAYING)
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            connectionCallback.onConnectionSuspended()

        }
    }


    @Suppress("PropertyName")
    val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
        .build()

    @Suppress("PropertyName")
    val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
        .build()


}