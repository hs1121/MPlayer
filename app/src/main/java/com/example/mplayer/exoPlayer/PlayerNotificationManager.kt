package com.example.mplayer.exoPlayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.session.MediaController
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mplayer.Constants.CHANNEL_ID
import com.example.mplayer.Constants.CHANNEL_NAME
import com.example.mplayer.Constants.NOTIFICATION_ID
import com.example.mplayer.R
import com.google.android.exoplayer2.ExoPlayer


import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

import com.google.common.reflect.Reflection.getPackageName


class PlayerNotificationManager(
    private val musicService: MusicService
) {

    private var iconUri: Uri?=null
    private var bitmap:Bitmap?=null

    private val glide=Glide.with(musicService).applyDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.place_holder)
            .error(R.drawable.place_holder)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )


    private var notificationManager:PlayerNotificationManager;

    init {

        val mediaController =MediaControllerCompat(musicService, musicService.sessionToken!!)

        val builder = PlayerNotificationManager.Builder(musicService, NOTIFICATION_ID, CHANNEL_ID)
        with (builder) {
            setMediaDescriptionAdapter(PlayerDescriptionAdapter(mediaController))
            setNotificationListener(PlayerNotificationListener(musicService))
            setChannelNameResourceId(R.string.CHANNEL_NAME)
            setChannelDescriptionResourceId(R.string.CHANNEL_DESCRIPTION)
        }
        notificationManager = builder.build()
        musicService.sessionToken?.let { notificationManager.setMediaSessionToken(it) }
        notificationManager.setSmallIcon(R.drawable.ic_full_logo)
        notificationManager.setUseRewindAction(false)
        notificationManager.setUseFastForwardAction(false)

    }



    fun getNotificationManager():PlayerNotificationManager= notificationManager
    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    fun showNotificationForPlayer(player: ExoPlayer){
        notificationManager.setPlayer(player)
    }

    private inner class PlayerDescriptionAdapter(val mediaController: MediaControllerCompat): PlayerNotificationManager.MediaDescriptionAdapter{
        override fun getCurrentContentTitle(player: Player): CharSequence {
          return  mediaController.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? =  mediaController.sessionActivity


        override fun getCurrentContentText(player: Player): CharSequence? =
           mediaController.metadata.description.subtitle.toString()

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            // check if changes are made to same song (seek position changed)
            // to avoid unnecessary updating of icon gives smooth UX of  notification
            if (iconUri==mediaController.metadata.description.iconUri)
                return bitmap

            glide.asBitmap()
                .load(mediaController.metadata.description.iconUri)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(p0: Bitmap, p1: Transition<in Bitmap>?) {
                        bitmap=p0
                        iconUri=mediaController.metadata.description.iconUri
                        callback.onBitmap(p0)
                    }
                    override fun onLoadCleared(p0: Drawable?) {
                        Log.i("hi","ye")
                    }

                })
            val uri =
                Uri.parse("android.resource://" + musicService.packageName.toString() + "/" + R.drawable.place_holder)
            return  MediaStore.Images.Media.getBitmap(musicService.contentResolver, uri);
        }

    }
}