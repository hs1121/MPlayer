package com.example.mplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mplayer.databinding.ActivitySettingBinding

import android.graphics.Color


import com.example.mplayer.exoPlayer.MusicService
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import androidx.lifecycle.lifecycleScope
import com.example.mplayer.Utility.EqualizerData
import com.example.mplayer.Utility.EqualizerFragmentV2
import com.example.mplayer.Utility.PreferenceDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var equalizerFragment: EqualizerFragmentV2

    @Inject
    lateinit var preferenceDataStore:PreferenceDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MusicService.playerInstance.observe(this) {
            it?.let {
                exoPlayer = it
                lifecycleScope.launch {
                   val data= preferenceDataStore.readEqualizerData()
                    setFragment(data)
                }

            }
        }


    }



    private fun setFragment(data: EqualizerData) {

        val sessionId: Int = exoPlayer.audioSessionId
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE;
        //EqualizerFragment.
        equalizerFragment = EqualizerFragmentV2.newBuilder(preferenceDataStore,data)
            .setAccentColor(getColor(R.color.white))
            .setAudioSessionId(sessionId)
            .build()

        supportFragmentManager.beginTransaction()
            .replace(R.id.eqFrame, equalizerFragment)
            .commit()
        //    equalizerFragment.view?.rootView?.background= AppCompatResources.getDrawable(this,R.drawable.custom_card_gb)

    }

    fun saveEqDAta(equalizerData: EqualizerData) {
        lifecycleScope.launch {
            preferenceDataStore.saveEqData(equalizerData)
        }
    }


}

