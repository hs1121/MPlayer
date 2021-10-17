package com.example.mplayer

import android.app.Activity
import android.media.session.PlaybackState
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.mplayer.Utility.isPlayEnabled
import com.example.mplayer.Utility.isPlaying
import com.example.mplayer.databinding.ActivityPlayerBinding
import com.example.mplayer.exoPlayer.MusicService
import com.example.mplayer.viewModels.PlayerViewModel
import com.google.android.exoplayer2.SimpleExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPlayerBinding
    private lateinit var playerViewModel: PlayerViewModel
    @Inject
    lateinit var glide: RequestManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPlayerBinding.inflate(layoutInflater)
        window.statusBarColor=getColor(R.color.background_color)
        setContentView(binding.root)
        playerViewModel=ViewModelProvider(this).get(PlayerViewModel::class.java)
        MusicService.playerInstance.observe(this,{
            if (playerViewModel.exoPlayer==null&& it !=null){
                playerViewModel.exoPlayer=it
               binding.playerView.player=it
            }
        })
        playerViewModel.exoPlayer?.let { binding.playerView.player=it }
        val playPause: ImageView = findViewById(R.id.play_pause)
       playPause.setOnClickListener { playerViewModel.playPause() }

        playerViewModel.isPlaying.observe(this,{ playbackState ->
            if (playbackState != null) {
                when {
                    playbackState.isPlaying -> playPause.setImageResource(R.drawable.exo_controls_pause)
                    else -> playPause.setImageResource(R.drawable.exo_controls_play)
                }
            }
        })

        playerViewModel.songMetaData.observe(this,{ item ->
            binding.titleName.text=item.description.title
            binding.artistName.text=item.description.subtitle
            item?.description?.iconUri.let { glide.load(it).into(binding.songImage) }

        })
    }
}