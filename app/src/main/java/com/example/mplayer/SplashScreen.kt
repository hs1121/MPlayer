package com.example.mplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.example.mplayer.Utility.Util
import com.example.mplayer.exoPlayer.MusicSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {

    @Inject
    lateinit var musicSource: MusicSource
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(savedInstanceState == null) {
            if (Util.isStoragePermissionGranted(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    Constants.PERMISSION_READ_INTERNAL_STORAGE
                )
            ) {
                lifecycleScope.launch {
                    musicSource.getSongs()
                    var intent = Intent(this@SplashScreen, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            else
                finish()
        }

    }
}