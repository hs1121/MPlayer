package com.example.mplayer.database

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.example.mplayer.Constants
import com.example.mplayer.Constants.ALBUMS_ROOT
import com.example.mplayer.Constants.PLAYLIST_ROOT
import com.example.mplayer.Constants.TRACKS_ROOT
import com.example.mplayer.Utility.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class BrowsingTree @Inject constructor( val musicSource: MusicSource ,
 val preferenceDataStore: PreferenceDataStore) {
    var browsingList = mutableMapOf<String, MutableList<MediaMetadataCompat>>()

    // creates a browsing tree in key value pair for easy navagation
    private fun setTree() {
        browsingList = mutableMapOf()

        val trackRoot = browsingList[TRACKS_ROOT] ?: mutableListOf()
        trackRoot.addAll(musicSource.tracks)

        val albumRoot = browsingList[ALBUMS_ROOT] ?: mutableListOf()
        musicSource.albums.forEach {
            val album: MediaMetadataCompat = MediaMetadataCompat.Builder().apply {
                flag = (MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
                from = ALBUMS_ROOT  // extension function to store that this item belongs to which key
            }
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, it.key)
                .putText(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, it.key)
                .putText(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                    it.value[0].description.iconUri.toString()
                ).build()


            if (it.value.isNotEmpty()) {
                albumRoot.add(album)
                browsingList[it.key] = it.value
            }
        }

        val playlistRoot = browsingList[PLAYLIST_ROOT] ?: mutableListOf()
        musicSource.playLists.forEach { it ->
            val playlist: MediaMetadataCompat = MediaMetadataCompat.Builder().apply {
                flag = (MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
                from = PLAYLIST_ROOT
            }
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, it.key)
                .putText(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, it.key)
                .putText(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                    it.value.let { item-> return@let if (item.isNotEmpty()) item[0].description.iconUri else ""}.toString()
                ).build()


            playlistRoot.add(playlist)
            browsingList[it.key] = it.value
        }

        browsingList[TRACKS_ROOT] = trackRoot
        browsingList[ALBUMS_ROOT] = albumRoot
        browsingList[PLAYLIST_ROOT] = playlistRoot

        GlobalScope.launch {
            val data=preferenceDataStore.readSotData()
            sortTracks(data)
            musicSource.setStateInitialized()
        }



    }
    fun sortTracks(sortData: SortData){
        browsingList[TRACKS_ROOT]=Util.sortBrowsingTree(browsingList[TRACKS_ROOT]!!,sortData)
    }



    suspend fun getMedia() {
        withContext(Dispatchers.IO){
            musicSource.getSongs()
            musicSource.getPlaylist()
            setTree()

        }

    }

     fun mediaReset(list: MutableList<Uri>){
         browsingList.forEach{ entry ->
        list.forEach{ uri->
           browsingList[entry.key]?.remove(entry.value.find { uri==it.description.mediaUri })
        }
            if (entry.value.isEmpty())
                 browsingList[ALBUMS_ROOT]?.remove(browsingList[ALBUMS_ROOT]?.find { entry.key==it.description.mediaId })
         }

    }

    suspend fun updatePlaylist()= withContext(Dispatchers.IO){
        musicSource.getPlaylist()
        setTree()
    }

        // check if the key is not used previously
    fun isRootAvailable(id:String):Boolean{
        return !browsingList.containsKey(id)
    }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return musicSource.whenReady(action)
    }


    // Media source which allows to back to back playing
    fun asMusicSource(
        dataSourceFactory: DefaultDataSource.Factory,
        key: String?
    ): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        browsingList[key]?.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(song.description.mediaUri!!))
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    // used in onLoadChildren function as mediaSource
    fun asMediaItem(key: String) = browsingList[key]?.map { song ->
       Util.asMediaItem(song)
    }?.toMutableList()



    fun musicItem(key: String) = browsingList[key]

    fun mediaListByItem(item: MediaMetadataCompat?): MutableList<MediaMetadataCompat> {
        val key = item?.from
        return browsingList[key] ?: mutableListOf()

    }
    fun getMediaItem(key:String,name:String){

    }


}