package com.example.mplayer.database

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.example.mplayer.Constants
import com.example.mplayer.Constants.ALBUMS_ROOT
import com.example.mplayer.Constants.PLAYLIST_ROOT
import com.example.mplayer.Constants.TRACKS_ROOT
import com.example.mplayer.Utility.flag
import com.example.mplayer.Utility.from
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class BrowsingTree @Inject constructor( val musicSource: MusicSource) {
    var browsingList = mutableMapOf<String, MutableList<MediaMetadataCompat>>()


    private fun setTree() {
        browsingList = mutableMapOf()

        val trackRoot = browsingList[TRACKS_ROOT] ?: mutableListOf()
        trackRoot.addAll(musicSource.tracks)

        val albumRoot = browsingList[ALBUMS_ROOT] ?: mutableListOf()
        musicSource.albums.forEach {
            val album: MediaMetadataCompat = MediaMetadataCompat.Builder().apply {
                flag = (MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
                from = ALBUMS_ROOT
            }
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, it.key)
                .putText(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, it.key)
                .putText(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                    it.value[0].description.iconUri.toString()
                ).build()


            albumRoot.add(album)
            browsingList[it.key] = it.value
        }

        val playlistRoot = browsingList[PLAYLIST_ROOT] ?: mutableListOf()
        musicSource.playLists.forEach {
            val playlist: MediaMetadataCompat = MediaMetadataCompat.Builder().apply {
                flag = (MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
                from = PLAYLIST_ROOT
            }
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, it.key)
                .putText(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, it.key)
                .putText(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                    it.value[0].description.iconUri.toString()
                ).build()


            playlistRoot.add(playlist)
            browsingList[it.key] = it.value
        }

        browsingList[TRACKS_ROOT] = trackRoot
        browsingList[ALBUMS_ROOT] = albumRoot
        browsingList[PLAYLIST_ROOT] = playlistRoot

        musicSource.setStateInitialized()

    }


    suspend fun getMedia() {
        withContext(Dispatchers.IO){
            musicSource.getSongs()
            musicSource.getPlaylist()
            setTree()

        }

    }

    suspend fun updatePlaylist()= withContext(Dispatchers.IO){
        musicSource.getPlaylist()
        setTree()
    }

    fun isRootAvailable(id:String):Boolean{
        return !browsingList.containsKey(id)
    }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return musicSource.whenReady(action)
    }


    fun asMusicSource(
        dataSourceFactory: DefaultDataSourceFactory,
        key: String?
    ): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        browsingList[key]?.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(song.description.mediaUri!!))
            //TODO: check if it works or not
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItem(key: String) = browsingList[key]?.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .setMediaUri(song.description.mediaUri)
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
        desc.from = song.from

        MediaBrowserCompat.MediaItem(
            desc.build(),
            song.bundle.getLong(Constants.METADATA_KEY_FLAG).toInt()
        )
    }?.toMutableList()

    fun musicItem(key: String) = browsingList[key]
    fun mediaListByItem(item: MediaMetadataCompat?): MutableList<MediaMetadataCompat> {
        val key = item?.from
        return browsingList[key] ?: mutableListOf()

    }


}