package com.example.mplayer.exoPlayer

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.ContentUris
import android.media.browse.MediaBrowser
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat

import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.example.mplayer.Utility.PathFromUri.getPathFromUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class MusicSource @Inject constructor(@ApplicationContext private val context: Context) {

     var musicItems: MutableList<MediaMetadataCompat> = mutableListOf()
    private val onReadyListeners= mutableListOf<(Boolean)-> Unit>()

    private var state:State= State.CREATED
        set(value){
            if(value==State.INITIALIZED|| value==State.ERROR){
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

    suspend fun  getSongs()= withContext(Dispatchers.IO){
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


                    val mediaItem=MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,artist)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE,name)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,name)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,pathUri.toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,albumArt.toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_ART_URI,album)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,artist)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,artist)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,path)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,albumArt.toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_DATE,dateAdded.toString())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,duration)
                        .build()

                    val pathList=ArrayList<String>()
                    pathList.add("/storage/emulated/0/MIUI/sound_recorder/call_rec")
                    //TODO: make it user selectable folders to stop getting media
                    if (!path?.contains(pathList[0])!!) {
                        musicItems.add(mediaItem)
                    }

                }
                state=State.INITIALIZED





            }
        }

    }

    fun asMusicSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource{
        val concatenatingMediaSource=ConcatenatingMediaSource()
        musicItems.forEach{ song ->
            val mediaSource=ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(song.description.mediaUri!!))
            //TODO: check if it works or not
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItem()=musicItems.map { song->
        val desc= MediaDescriptionCompat.Builder()
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .setMediaUri(song.description.mediaUri)
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .build()
        MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }.toMutableList()




    fun whenReady(action: (Boolean)-> Unit):Boolean{
        return if(state==State.CREATED||state==State.INITIALIZING){
            onReadyListeners+=action
            false
        }else{
            action(state==State.INITIALIZED)
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