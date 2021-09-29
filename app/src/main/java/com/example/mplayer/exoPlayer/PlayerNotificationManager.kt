package com.example.mplayer.exoPlayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mplayer.Constants.CHANNEL_ID
import com.example.mplayer.Constants.NOTIFICATION_ID
import com.example.mplayer.R
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class PlayerNotificationManager(
    private val context: Context,
    private val sessionToken:MediaSessionCompat.Token
) {

    private var mediaController:MediaControllerCompat = MediaControllerCompat(context, sessionToken)

    private lateinit var notificationManager:PlayerNotificationManager;

    init {
        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            CHANNEL_ID,
            R.string.CHANNEL_NAME,
            R.string.CHANNEL_DESCRIPTION,
            NOTIFICATION_ID,
            PlayerDescriptionAdapter()
        )
    }

    fun getNotificationManager():PlayerNotificationManager= notificationManager

    private inner class PlayerDescriptionAdapter: PlayerNotificationManager.MediaDescriptionAdapter{
        override fun getCurrentContentTitle(player: Player): CharSequence = mediaController.metadata.description.title.toString()

        override fun createCurrentContentIntent(player: Player): PendingIntent? =mediaController.sessionActivity


        override fun getCurrentContentText(player: Player): CharSequence? =mediaController.metadata.description.subtitle

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            Glide.with(context).asBitmap()
                .load(mediaController.metadata.description.iconUri)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(p0: Bitmap, p1: Transition<in Bitmap>?) {
                        callback.onBitmap(p0)
                    }
                    override fun onLoadCleared(p0: Drawable?) =Unit

                })
            return null
        }

    }
}