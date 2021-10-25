package com.example.mplayer.Utility

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.example.mplayer.Constants
import com.example.mplayer.Constants.ALBUMS_ROOT
import com.example.mplayer.Constants.TRACKS_ROOT
import com.example.mplayer.exoPlayer.MusicSource
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import javax.inject.Inject

class BrowsingTree @Inject constructor(private val musicSource: MusicSource) {
     var browsingList= mutableMapOf<String,MutableList<MediaMetadataCompat>>()
    private var state= MusicSource.State.INITIALIZING


    private fun setTree(){

            val trackRoot = browsingList[TRACKS_ROOT] ?: mutableListOf()
            trackRoot.addAll(musicSource.musicItems)

            val albumRoot = browsingList[ALBUMS_ROOT] ?: mutableListOf()
            musicSource.albums.forEach{
                val album:MediaMetadataCompat =MediaMetadataCompat.Builder().apply {
                    flag=(MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
                    from= ALBUMS_ROOT}
                    .putText(MediaMetadataCompat.METADATA_KEY_ALBUM,it.key)
                    .putText(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,it.key)
                    .putText(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                        it.value[0].description.iconUri.toString()
                    ).build()


                albumRoot.add(album)
                browsingList[it.key]=it.value
            }

            browsingList[TRACKS_ROOT]=trackRoot
            browsingList[ALBUMS_ROOT]=albumRoot

        musicSource.setStateInitialized()

    }

    suspend fun getMedia(){
        musicSource.getSongs()
        setTree()
    }
    fun whenReady(action:(Boolean)->Unit):Boolean{
       return musicSource.whenReady(action)
    }


    fun asMusicSource(dataSourceFactory: DefaultDataSourceFactory,key:String): ConcatenatingMediaSource {
        val concatenatingMediaSource= ConcatenatingMediaSource()
        browsingList[key]?.forEach{ song ->
            val mediaSource= ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(song.description.mediaUri!!))
            //TODO: check if it works or not
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItem(key:String)=browsingList[key]?.map { song->
        val desc= MediaDescriptionCompat.Builder()
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .setMediaUri(song.description.mediaUri)
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            desc.from=song.from

        MediaBrowserCompat.MediaItem(desc.build(),song.bundle.getLong(Constants.METADATA_KEY_FLAG).toInt())
    }?.toMutableList()

    fun musicItem()=browsingList[TRACKS_ROOT]
    fun mediaListByItem(item:MediaMetadataCompat?):MutableList<MediaMetadataCompat>{
        val key=item?.from
        return browsingList[key]?: mutableListOf()

    }



}