package com.example.mplayer.viewModels

import android.content.ContentValues.TAG
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.lifecycle.*
import com.example.mplayer.Constants
import com.example.mplayer.Constants.ALBUMS_ROOT
import com.example.mplayer.Constants.TRACKS_ROOT
import com.example.mplayer.Utility.*
import com.example.mplayer.exoPlayer.MediaSessionConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val browsingTree: BrowsingTree,
    private val mediaSessionConnection: MediaSessionConnection
) : ViewModel() {

    private var _tracksList = MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
    var tracksList: LiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>> = _tracksList
    private val _albumList = MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
    val albumList: LiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>> = _albumList

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
                    else -> _songList.postValue(Event(children).also { it.handle() })
                }

            }
        })

    }



    fun playMedia(mediaItem: MediaBrowserCompat.MediaItem, isPauseEnable: Boolean = true) {

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

    fun itemClicked(item: MediaBrowserCompat.MediaItem) {
        if (item.isPlayable) playMedia(item,false)
        else{
            getMedia(item.description.mediaId.toString())

        }
    }

    fun resetSongList() {
        _songList = MutableLiveData<Event<MutableList<MediaBrowserCompat.MediaItem>>>()
        songList = _songList
    }


}