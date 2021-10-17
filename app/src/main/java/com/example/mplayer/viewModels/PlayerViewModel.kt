package com.example.mplayer.viewModels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mplayer.Utility.isPlaying
import com.example.mplayer.exoPlayer.MediaSessionConnection
import com.example.mplayer.exoPlayer.MusicService
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerControlView
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.reflect.Array.set
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
     val mediaSessionConnection: MediaSessionConnection
):ViewModel() {

    val songMetaData=mediaSessionConnection.nowPlaying
    val isPlaying=mediaSessionConnection.playbackState
          var exoPlayer: SimpleExoPlayer?=null



    fun playPause() {
        mediaSessionConnection.playbackState.value?.let { playbackState ->
            when {
                playbackState.isPlaying -> {
                    mediaSessionConnection.transportControls.pause()
                }
                else -> {
                    mediaSessionConnection.transportControls.play()
                }
            }
        }
    }


}