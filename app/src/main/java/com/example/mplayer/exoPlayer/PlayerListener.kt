package com.example.mplayer.exoPlayer

import android.widget.Toast
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player

class PlayerListener(
    private val musicService: MusicService ):Player.EventListener {
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if(playbackState==Player.STATE_READY&& !playWhenReady)
            musicService.stopForeground(false)
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "Oops Player Not Prepared", Toast.LENGTH_SHORT).show()
    }
}