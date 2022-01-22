package com.example.mplayer.exoPlayer

import android.widget.Toast
import com.example.mplayer.Utility.PreferenceDataStore
import com.example.mplayer.Utility.from
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerListener(
    private val musicService: MusicService,
    private val playerNotificationManager: PlayerNotificationManager,
    private val preferenceDataStore: PreferenceDataStore):Player.Listener {



    private var isLooping=false;

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
    when (playbackState) {
        Player.STATE_BUFFERING,
        Player.STATE_READY -> {
            playerNotificationManager.showNotificationForPlayer(musicService.exoPlayer)
            if (playbackState == Player.STATE_READY) {

                // save current song data so that its displayed after reboot
                    if (!isLooping) {
                        isLooping=true
                        musicService.serviceScope.launch() {
                            saveSongState()
                        }
                    }


                if (!playWhenReady) {
                    //musicService.stopForeground(false)
                    //musicService.isForegroundService = false
                    // :::: Not required since notification can be swiped without stopping
                    // foreground service and  with these options when app force killed from recent tab
                    // , the notification do not clears and create null exception and also bad UX
                }
            }
        }
        else -> {
            playerNotificationManager.hideNotification()
        }
    }
}




    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        if(!isLooping) {
            isLooping=true
            musicService.serviceScope.launch {
                saveSongState()
            }
        }
    }

    private suspend fun saveSongState(){

        val song=musicService.currentlyPlayingSong!!
        val time= musicService.exoPlayer.currentPosition
        preferenceDataStore.saveCurrentSongData(
            song.description.mediaId!!,song.from,time)
        if(musicService.exoPlayer.isPlaying) {
            delay(5000)
            saveSongState()
        }else{
            isLooping=false
        }
    }



    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "Oops Player Not Prepared", Toast.LENGTH_SHORT).show()
    }
}