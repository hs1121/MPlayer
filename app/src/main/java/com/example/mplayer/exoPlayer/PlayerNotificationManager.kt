package com.example.mplayer.exoPlayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mplayer.Constants.CHANNEL_ID
import com.example.mplayer.Constants.NOTIFICATION_ID
import com.example.mplayer.R
import com.google.android.exoplayer2.DefaultControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

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

    private var mediaController:MediaControllerCompat = MediaControllerCompat(musicService,
        musicService.sessionToken!!
    )

    private lateinit var notificationManager:PlayerNotificationManager;

    init {
        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
            musicService,
            CHANNEL_ID,
            R.string.CHANNEL_NAME,
            R.string.CHANNEL_DESCRIPTION,
            NOTIFICATION_ID,
            PlayerDescriptionAdapter(),
            PlayerNotificationListener(musicService)
        ).apply {
            musicService.sessionToken?.let { setMediaSessionToken(it) }
            setSmallIcon(R.drawable.ic_full_logo)
            setControlDispatcher(DefaultControlDispatcher(0, 0))

        }


//        notificationManager = PlayerNotificationManager.Builder(musicService, NOTIFICATION_ID,
//            CHANNEL_ID,PlayerDescriptionAdapter())
//            .setChannelNameResourceId(R.string.CHANNEL_NAME)
//            .setChannelDescriptionResourceId(R.string.CHANNEL_DESCRIPTION)
//            .setNotificationListener(PlayerNotificationListener(musicService))
//            .build().apply {
//                musicService.sessionToken?.let { setMediaSessionToken(it) }
//                setSmallIcon(R.drawable.ic_full_logo)
//                setControlDispatcher(DefaultControlDispatcher(0, 0))
//            }
    }

    fun getNotificationManager():PlayerNotificationManager= notificationManager

    private inner class PlayerDescriptionAdapter: PlayerNotificationManager.MediaDescriptionAdapter{
        override fun getCurrentContentTitle(player: Player): CharSequence {
          return  mediaController.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? = musicService.activityIntent
        //    mediaController.sessionActivity


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
                    override fun onLoadCleared(p0: Drawable?) =Unit

                })
            return null
        }

    }
}