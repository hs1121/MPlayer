package com.example.mplayer.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.mplayer.Constants
import com.example.mplayer.R
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class App : Application(){
//    override fun onCreate() {
//        super.onCreate()
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel= NotificationChannel(
//                Constants.CHANNEL_ID, Constants.CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_LOW)
//            channel.description=this.getString(R.string.CHANNEL_DESCRIPTION)
//            val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            manager.createNotificationChannel(channel)
//        } else {
//            TODO("VERSION.SDK_INT < O")
//        }
//    }
}