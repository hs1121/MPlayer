package com.example.mplayer.exoPlayer

import android.app.Notification
import android.content.Intent
import android.provider.SyncStateContract
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.mplayer.Constants
import com.example.mplayer.Constants.NOTIFICATION_ID
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import java.lang.Exception

class PlayerNotificationListener(
    private val musicService: MusicService
) : PlayerNotificationManager.NotificationListener {



    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        musicService.apply {  // stop playback and service when notification is removed
            stopForeground(true)
             isForegroundService = false
            Log.i("Notification","OnNotificationCanceled")
            stopSelf()
        }
    }


    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        musicService.apply {
            if (ongoing&&!isForegroundService){
                // start service if notification is posted and service is not started
                ContextCompat.startForegroundService(this,
                Intent(applicationContext,MusicService::class.java))
                isForegroundService=true
                startForeground(notificationId,notification) // notification already places


            }

        }

   }
}