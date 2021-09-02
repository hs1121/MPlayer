package com.example.mplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(savedInstanceState == null) {
            lifecycleScope.launch {
                delay(2000)
                var intent = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(intent)
            }
        }
        else
            finish()
    }



}