package com.example.mplayer.exoPlayer

import android.util.Log
import android.widget.Toast
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray

class PlayerListener(
    private val musicService: MusicService ):Player.EventListener {
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
//        super.onPlaybackStateChanged(playbackState)
//        super.onPlayWhenReadyChanged(playWhenReady,playbackState)
        if(playbackState==Player.STATE_READY&& !playWhenReady) // stop service if player is not ready and remove notification
            musicService.stopForeground(false)
    }



    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "Oops Player Not Prepared", Toast.LENGTH_SHORT).show()
    }
}