package com.example.mplayer.viewModels

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.*
import com.example.mplayer.Constants
import com.example.mplayer.Constants.ALBUMS_ROOT
import com.example.mplayer.Constants.PLAYLIST_ROOT
import com.example.mplayer.Constants.TRACKS_ROOT
import com.example.mplayer.PlayerActivity
import com.example.mplayer.Utility.*
import com.example.mplayer.database.Repository
import com.example.mplayer.database.entity.PlaylistEntity
import com.example.mplayer.exoPlayer.MediaSessionConnection
import com.example.mplayer.exoPlayer.MusicService
import com.google.android.exoplayer2.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val mediaSessionConnection: MediaSessionConnection,
    private val preferenceDataStore: PreferenceDataStore
) : ViewModel() {


    val currentlyPlayingSong = mediaSessionConnection.nowPlaying
    val isPlaying = mediaSessionConnection.playbackState
    private var currentSongData: CurrentSongData? = null
    var exoPlayer: ExoPlayer? = null
    val onControllerReady=mediaSessionConnection.onControllerReady


    private var _tracksList = MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
    var tracksList: LiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>> = _tracksList

    private val _albumList = MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
    val albumList: LiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>> = _albumList

    private var _playlist = MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
    val playlist: LiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>> = _playlist


    private var _songList = MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
    var songList: LiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>> = _songList


    private lateinit var sortData: SortData
    private  var repeatMode:Int=ExoPlayer.REPEAT_MODE_OFF
    private  var shuffleMode=false


    init {
        mediaSessionConnection //init media session
        viewModelScope.launch {
            currentSongData = preferenceDataStore.readCurrentSongData()
            sortData = preferenceDataStore.readSotData()


        }

    }


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

    fun insertPlaylist(playlistEntity: PlaylistEntity, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPlaylist(playlistEntity)
            repository.browsingTree.updatePlaylist()
            resetPlaylist()
            withContext(Dispatchers.Main) {
                callBack()
            }
        }

    }

    suspend fun getPlayList(id: String): PlaylistEntity? = repository.getPlaylist(id)


    fun deletePlaylists(name: MutableList<String>, callBack: () -> Unit) {
        viewModelScope.launch {
            repository.deletePlaylist(name)
            repository.browsingTree.updatePlaylist()
            resetPlaylist()
            withContext(Dispatchers.Main) {
                callBack()
            }
        }
    }

    fun updatePlaylistItem(item: MutableList<String?>, name: String, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePlaylistITem(item, name)
            repository.browsingTree.updatePlaylist()
            withContext(Dispatchers.Main) {
                callBack()
            }
        }
    }

    fun updatePlaylist(playlistEntity: PlaylistEntity, callBack: () -> Unit) {
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
                    TRACKS_ROOT -> {
                        val sortedChildren = Util.sortList(children, sortData)
                        _tracksList.postValue(Event(sortedChildren).also { it.handle() })

                    }
                    ALBUMS_ROOT -> _albumList.postValue(Event(children).also { it.handle() })
                    PLAYLIST_ROOT -> _playlist.postValue(Event(children).also { it.handle() })

                    else -> _songList.postValue(Event(children).also { it.handle() })
                }

            }
        })

    }

    private fun restoreCurrentMediaConfig(mediaId: String, from: String?, seekTo: Long?) {
        // set media without playing (initial setup for song played last time)
        val transportControls = mediaSessionConnection.transportControls
        transportControls.playFromMediaId(
            mediaId,
            Bundle().apply {
                putString(Constants.METADATA_KEY_FROM, from ?: "")
                putLong(Constants.SEEK_TO, seekTo ?: 0)
                putBoolean(Constants.PLAY_MEDIA, false)
            })
        transportControls.pause()
    }

    private fun playOrderMedia() {
        // update media when the order is changed
        MusicService.currentSong?.let {
            if (it.from == TRACKS_ROOT) {
                val mediaItem = Util.asMediaItem(it)
                val transportControls = mediaSessionConnection.transportControls
                transportControls.playFromMediaId(
                    mediaItem.mediaId,
                    Bundle().apply {
                        putString(Constants.METADATA_KEY_FROM, mediaItem.from)
                        putLong(
                            Constants.SEEK_TO,
                            (exoPlayer?.let { it.currentPosition }) ?: 0L
                        ) // 150 sec to cover glitch
                        putBoolean(Constants.PLAY_MEDIA, exoPlayer?.isPlaying ?: false)

                        // todo: fix the glitch (execute it at end or when track changed )
                    }
                )

            }
        }
    }

    private fun playMedia(mediaItem: MediaBrowserCompat.MediaItem, isPauseEnable: Boolean = true) {

        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        val transportControls = mediaSessionConnection.transportControls

        if (isPrepared && mediaItem.mediaId == nowPlaying?.description?.mediaId&& mediaItem.from==nowPlaying?.from) {
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
                Bundle().apply {
                    putString(Constants.METADATA_KEY_FROM, mediaItem.from)
                    putLong(Constants.SEEK_TO, -1L)
                })
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

    fun itemClicked(item: MediaBrowserCompat.MediaItem, context: Context) {
        if (item.isPlayable) {
            playMedia(item, false)
            context.startActivity(Intent(context, PlayerActivity::class.java))
        } else {
            getMedia(item.description.mediaId.toString())

        }
    }

    private val _mediaRemoved = MutableLiveData<Boolean>()
    val mediaRemoved: LiveData<Boolean> = _mediaRemoved
    fun mediaRemoved(boolean: Boolean) {
        _mediaRemoved.postValue(boolean)
    }

    fun mediaRemoved(list: MutableList<Uri>, callBack: () -> Unit) {
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

    fun refreshMedia() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.browsingTree.getMedia()
            initMedia()
        }
    }

    fun updateSort(isAsc: Boolean?, itemId: Int?) {
        sortData.apply {
            if (itemId != null) sortData.id = itemId
            else sortData.isChecked = isAsc!!
        }
        val tracks = tracksList.value?.data
        tracks?.apply {
            val sortedList = Util.sortList(this, sortData)
            val event = Event(sortedList).also { it.handle() }
            _tracksList.postValue(event)
            viewModelScope.launch {
                repository.updateSort(sortData) {
                    viewModelScope.launch {
                        repository.updateSort(sortData) {
                            playOrderMedia()
                        }
                    }

                }
            }
        }
    }


    fun initLastPlayedMedia() {

        currentSongData?.apply {
            if (mediaId != null && mediaId.isNotEmpty())
                restoreCurrentMediaConfig(mediaId, from, time)
        }
    }
    fun initPlayModes(){
        viewModelScope.launch {
            repeatMode = preferenceDataStore.readRepeatMode()
            shuffleMode = preferenceDataStore.readShuffleMode()
            exoPlayer?.shuffleModeEnabled = shuffleMode
            exoPlayer?.repeatMode = repeatMode
        }
    }


}