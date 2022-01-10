package com.example.mplayer.database

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.content.ContentUris
import android.support.v4.media.MediaBrowserCompat

import android.support.v4.media.MediaMetadataCompat
import com.example.mplayer.Constants
import com.example.mplayer.Utility.PathFromUri.getPathFromUri
import com.example.mplayer.Utility.flag
import com.example.mplayer.Utility.from
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MusicSource @Inject constructor(
    @ApplicationContext val context: Context,
    private val playerDatabase: MPlayerDatabase
) {

    var mediaItems: MutableList<MediaMetadataCompat.Builder> = mutableListOf()
     var tracks: MutableList<MediaMetadataCompat> = mutableListOf()
     var albums:HashMap<String,MutableList<MediaMetadataCompat>> = HashMap()
    var playLists:HashMap<String,MutableList<MediaMetadataCompat>> = HashMap()


    // a concept which allows us to keep the tasks stored as lambda function(as queue) until an
    // specific condition is not met(in this case until player is not ready)
    private val onReadyListeners= mutableListOf<(Boolean)-> Unit>()

    private var state: State = State.CREATED
        set(value){
            if(value== State.INITIALIZED || value== State.ERROR){
                synchronized(onReadyListeners){
                    field=value
                    onReadyListeners.forEach { listeners ->
                        listeners(value == State.INITIALIZED)
                    }
                }
            }else{
                field=value
            }
        }

    fun setStateInitialized(){
        state= State.INITIALIZED
    }

     fun  getSongs(){
        mediaItems= mutableListOf()
        tracks= mutableListOf()
        albums= HashMap()
        playLists = HashMap()
        val uri=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.contentResolver.query(uri,null,null,null)?.use { cursor->
                while (cursor.moveToNext()){

                    val isMusic=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC))
                    if (isMusic==0)
                        continue

                    val name=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                    val album=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                    val artist=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                    val dateAdded=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                    val duration=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))


                    val albumId=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                    val artworkUri = Uri.parse("content://media/external/audio/albumart")
                    val albumArt = ContentUris.withAppendedId(artworkUri, albumId)

                    val id=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    val pathUri:Uri =ContentUris.withAppendedId(uri,id)
                    val path=getPathFromUri(context,pathUri)


                    val mediaItem=MediaMetadataCompat.Builder().apply {
                        flag=(MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
                    }
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,artist)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE,name)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,name)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,pathUri.toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,albumArt.toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM,album)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,artist)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,artist)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,path)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,albumArt.toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_DATE,dateAdded.toString())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,duration)

                        mediaItems.add(mediaItem)



                    val pathList=ArrayList<String>()
                    pathList.add("/storage/emulated/0/MIUI/sound_recorder/call_rec")
                    //TODO: make it user selectable folders to stop getting media from that folder
                    if (!path?.contains(pathList[0])!!) {
                        val trackItem = mediaItem
                        trackItem.from = Constants.TRACKS_ROOT
                        tracks.add(trackItem.build())

                        mediaItem.from = album
                        val listItem = albums[album] ?: mutableListOf()
                        listItem.add(mediaItem.build())
                        albums[album] = listItem
                    }

                }


            }
        }
    }

      suspend fun getPlaylist() {
          playLists= hashMapOf()
            val idsList= playerDatabase.playlistDao().getPlaylists()?: mutableListOf()

            idsList.forEach{ entity->

                val itemList= mutableListOf<MediaMetadataCompat>()
                entity.data.forEach{ id ->
                   val item= mediaItems.find { id==it.build().description.mediaId }
                    item?.let {
                        val builder=it
                        builder.from=entity.name
                        itemList.add(builder.build())
                    }
                }
                playLists[entity.name]=itemList
            }
    }


    // executes the lambda function if player is ready else stores in the queue
    fun whenReady(action: (Boolean)-> Unit):Boolean{
        return if(state== State.CREATED ||state== State.INITIALIZING){
            onReadyListeners+=action
            false
        }else{
            action(state== State.INITIALIZED)
            true
        }
    }


    enum class State {
        CREATED,
        INITIALIZING,
        INITIALIZED,
        ERROR
    }





}