package com.example.mplayer.viewModels

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.lifecycle.*
import com.example.mplayer.Constants
import com.example.mplayer.Constants.ALBUMS_ROOT
import com.example.mplayer.Constants.PLAYLIST_ROOT
import com.example.mplayer.Constants.TRACKS_ROOT
import com.example.mplayer.PlayerActivity
import com.example.mplayer.Utility.*
import com.example.mplayer.database.BrowsingTree
import com.example.mplayer.database.Repository
import com.example.mplayer.database.entity.PlaylistEntity
import com.example.mplayer.exoPlayer.MediaSessionConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val mediaSessionConnection: MediaSessionConnection
) : ViewModel() {



    private var _tracksList = MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
    var tracksList: LiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>> = _tracksList

    private val _albumList = MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
    val albumList: LiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>> = _albumList

     private var _playlist =MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
     val playlist : LiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>  = _playlist


    private var _songList = MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
    var songList: LiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>> = _songList


    companion object {
        private var viewModelInstance: MainViewModel? = null
        fun getViewModel(context: ViewModelStoreOwner): MainViewModel {
            if (viewModelInstance == null) {
                viewModelInstance = ViewModelProvider(context).get(MainViewModel::class.java)
            }
            return viewModelInstance as MainViewModel
        }
    }

//    fun getPlaylist(){
//        viewModelScope.launch(Dispatchers.IO) {
//            val list=repository.playerDatabase.playlistDao().getAll()
//            _playlist.postValue(list)
//        }
//    }

    fun insertPlaylist(playlistEntity: PlaylistEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPlaylist(playlistEntity)
            repository.browsingTree.updatePlaylist()
        }

    }
//    fun deletePlaylist(playlistEntity: PlaylistEntity){
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.deletePlaylist(playlistEntity)
//        }
//        val list=playlist.value?: mutableListOf()
//        list.remove(playlistEntity)
//        _playlist.postValue(list)
//    }
//
//    fun updatePlaylist(playlistEntity: PlaylistEntity){
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.updatePlaylist(playlistEntity)
//        }
//        val list=playlist.value?: mutableListOf()
//        list[list.indexOf(list.find { it.name==playlistEntity.name })] = playlistEntity
//        _playlist.postValue(list)
//    }


    fun getMedia(key: String) {
        mediaSessionConnection.subscribe(key, object :
            MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                when (key) {
                    TRACKS_ROOT -> _tracksList.postValue(Event(children).also { it.handle() })
                    ALBUMS_ROOT -> _albumList.postValue(Event(children).also { it.handle() })
                    PLAYLIST_ROOT -> _playlist.postValue(Event(children).also { it.handle() })

                    else -> _songList.postValue(Event(children).also { it.handle() })
                }

            }
        })

    }

    private fun playMedia(mediaItem: MediaBrowserCompat.MediaItem, isPauseEnable: Boolean = true) {

        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        val transportControls = mediaSessionConnection.transportControls

        if (isPrepared && mediaItem.mediaId == nowPlaying?.description?.mediaId) {
            mediaSessionConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying ->
                        if (isPauseEnable) transportControls.pause() else Unit
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        Log.w(
                            TAG, "Playable item clicked but neither play nor pause are enabled!" +
                                    " (mediaId=${mediaItem.mediaId})"
                        )
                    }
                }
            }
        } else
            transportControls.playFromMediaId(
                mediaItem.mediaId,
                Bundle().apply { putString(Constants.METADATA_KEY_FROM, mediaItem.from) })
    }

    fun itemClicked(item: MediaBrowserCompat.MediaItem,context: Context) {
        if (item.isPlayable){
            playMedia(item,false)
            context.startActivity(Intent(context,PlayerActivity::class.java))
        }
        else{
            getMedia(item.description.mediaId.toString())

        }
    }

    fun resetSongList() {
        _songList = MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
        songList = _songList
    }

    fun resetPlaylist() {
        _playlist.postValue(Event(mutableListOf()))
    }

    fun initMedia() {
        getMedia(TRACKS_ROOT)
        getMedia(ALBUMS_ROOT)
        getMedia(PLAYLIST_ROOT)
    }


}