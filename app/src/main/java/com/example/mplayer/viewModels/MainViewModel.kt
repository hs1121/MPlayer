package com.example.mplayer.viewModels

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val mediaSessionConnection: MediaSessionConnection
) : ViewModel() {

    @Inject
    lateinit var preferenceDataStore:PreferenceDataStore
    val currentlyPlayingSong=mediaSessionConnection.nowPlaying
    val isPlaying=mediaSessionConnection.playbackState
    var exoPlayer: ExoPlayer?=null
    set(value) {
        field=value
        viewModelScope.launch {
            val data=preferenceDataStore.readCurrentSongData()
            data?.apply {
                restoreCurrentMediaConfig(mediaId,from,time)
                }
            }
        }


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

    fun insertPlaylist(playlistEntity: PlaylistEntity,callBack: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPlaylist(playlistEntity)
            repository.browsingTree.updatePlaylist()
            resetPlaylist()
            withContext(Dispatchers.Main){
                callBack()
            }
        }

    }

   suspend fun getPlayList(id:String):PlaylistEntity?= repository.getPlaylist(id)

    fun deletePlaylist(entity: PlaylistEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePlaylist(entity)
        }
    }
    fun deletePlaylists(name:MutableList<String>, callBack: () -> Unit){
        viewModelScope.launch {
            repository.deletePlaylist(name)
            repository.browsingTree.updatePlaylist()
            resetPlaylist()
            withContext(Dispatchers.Main){
                callBack()
            }
        }
    }
    fun updatePlaylistItem(item:MutableList<String?>,name:String,callBack:()->Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePlaylistITem(item, name)
            repository.browsingTree.updatePlaylist()
            withContext(Dispatchers.Main){
                callBack()
            }
        }
    }

    fun updatePlaylist(playlistEntity: PlaylistEntity ,callBack: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePlaylist(playlistEntity)
            repository.browsingTree.updatePlaylist()
            getMedia(PLAYLIST_ROOT)
            withContext(Dispatchers.Main) {
                callBack()
            }
        }

    }


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
    private fun restoreCurrentMediaConfig(mediaId:String,from:String,seekTo:Long){
        val transportControls = mediaSessionConnection.transportControls
        transportControls.playFromMediaId(
            mediaId,
            Bundle().apply { putString(Constants.METADATA_KEY_FROM, from)
            putLong(Constants.SEEK_TO,seekTo)})
        transportControls.pause()
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
                Bundle().apply { putString(Constants.METADATA_KEY_FROM, mediaItem.from)
                    putLong(Constants.SEEK_TO,-1L)})
    }

    fun playPause() {
        mediaSessionConnection.playbackState.value?.let { playbackState ->
            when {
                playbackState.isPlaying -> {
                    mediaSessionConnection.transportControls.pause()
                }
                else -> {
                    mediaSessionConnection.transportControls.play()
                }
            }
        }
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

    private val _mediaRemoved=MutableLiveData<Boolean>()
    val mediaRemoved:LiveData<Boolean> = _mediaRemoved
    fun mediaRemoved(boolean: Boolean){
        _mediaRemoved.postValue(boolean)
    }
    fun mediaRemoved(list:MutableList<Uri>,callBack: () -> Unit){
        viewModelScope.launch {
            repository.browsingTree.mediaReset(list)
            initMedia()
            callBack()
        }

    }
    fun resetSongList() {
        // reset the songList to make tell that it is not handled (if any update is made or user selects another album or playlist)
        _songList = MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
        songList = _songList
    }

    fun resetPlaylist() {
        _playlist.postValue(Event(mutableListOf()))
    }

    fun resetMedia() {
        _playlist.postValue(Event(mutableListOf()))
        _tracksList.postValue(Event(mutableListOf()))
        _albumList.postValue(Event(mutableListOf()))
    }

    fun initMedia() {
        getMedia(TRACKS_ROOT)
        getMedia(ALBUMS_ROOT)
        getMedia(PLAYLIST_ROOT)
    }
    fun getMedia(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.browsingTree.getMedia()
            initMedia()
        }
    }



}