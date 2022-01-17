package com.example.mplayer.di

import android.content.ComponentName
import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.mplayer.Constants.PLAYER_DATABASE
import com.example.mplayer.R
import com.example.mplayer.Utility.PreferenceDataStore
import com.example.mplayer.database.BrowsingTree
import com.example.mplayer.database.MPlayerDatabase
import com.example.mplayer.exoPlayer.MediaSessionConnection
import com.example.mplayer.exoPlayer.MusicService
import com.example.mplayer.database.MusicSource
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
    fun providePlayerDatabase(
        @ApplicationContext context: Context
    ):MPlayerDatabase{return Room.databaseBuilder(context,MPlayerDatabase::class.java,PLAYER_DATABASE).build()}


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
        @ApplicationContext context: Context,
        playerDatabase: MPlayerDatabase
    )= MusicSource(context,playerDatabase)



    @Singleton
    @Provides
    fun provideMediaSessionConnection(
        @ApplicationContext context: Context)= MediaSessionConnection(
        context,
        ComponentName(context,MusicService::class.java))

    @Singleton
    @Provides
    fun provideBrowsingTree(musicSource: MusicSource)= BrowsingTree(musicSource)

    @Singleton
    @Provides
    fun providePreferenceDataStore(
        @ApplicationContext context: Context)=PreferenceDataStore(context)

}