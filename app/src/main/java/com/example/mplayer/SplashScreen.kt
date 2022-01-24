package com.example.mplayer

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.example.mplayer.Constants.FROM_SPLASH_SCREEN
import com.example.mplayer.Utility.PreferenceDataStore
import com.example.mplayer.database.BrowsingTree
import com.example.mplayer.Utility.Util
import com.example.mplayer.exoPlayer.MediaSessionConnection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_READ_INTERNAL_STORAGE && permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivity()
        }
        else
            finish()
    }

    @Inject
    lateinit var musicSource: BrowsingTree
    @Inject
    lateinit var preferenceDataStore: PreferenceDataStore
    @Inject
    lateinit var mediaSessionConnection: MediaSessionConnection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (savedInstanceState == null) {
            mediaSessionConnection //  initializing
            if (Util.isStoragePermissionGranted(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    Constants.PERMISSION_READ_INTERNAL_STORAGE
                ) ){
                    startActivity()
            }
        }

    }

    private fun startActivity() {
        lifecycleScope.launch {
           val data= preferenceDataStore.readSotData()
            musicSource.getMedia()
            var intent = Intent(this@SplashScreen, MainActivity::class.java)
            intent.putExtra(FROM_SPLASH_SCREEN,true)
            startActivity(intent)
        }
    }




}
