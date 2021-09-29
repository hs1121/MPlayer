package com.example.mplayer.di

import android.content.ComponentName
import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.mplayer.R
import com.example.mplayer.exoPlayer.MediaSessionConnection
import com.example.mplayer.exoPlayer.MusicService
import com.example.mplayer.exoPlayer.MusicSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    )= Glide.with(context).applyDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.place_holder)
            .error(R.drawable.place_holder)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

    @Singleton
    @Provides
    fun provideMusicSource(
        @ApplicationContext context: Context
    )= MusicSource(context)

    @Singleton
    @Provides
    fun provideMediaSessionConnection(
        @ApplicationContext context: Context)= MediaSessionConnection(
        context,
        ComponentName(context,MusicService::class.java))
}