package com.example.mplayer.viewModels

import android.content.ContentValues.TAG
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.lifecycle.*
import com.example.mplayer.Constants.ROOT_PACKAGE
import com.example.mplayer.Utility.isPlayEnabled
import com.example.mplayer.Utility.isPlaying
import com.example.mplayer.Utility.isPrepared
import com.example.mplayer.exoPlayer.MediaSessionConnection
import com.example.mplayer.exoPlayer.MusicSource
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
     private val musicSource: MusicSource,
     private val mediaSessionConnection: MediaSessionConnection
): ViewModel() {

    private var _songList= MutableLiveData<MutableList<MediaBrowserCompat.MediaItem>>()
    var songList:LiveData<MutableList<MediaBrowserCompat.MediaItem>> = _songList

    companion object {
        private  var  viewModelInstance:MainViewModel?=null
        fun getViewModel(context: ViewModelStoreOwner):MainViewModel {
            if (viewModelInstance==null){
                viewModelInstance=ViewModelProvider(context).get(MainViewModel::class.java)
            }
            return viewModelInstance as MainViewModel
        }
    }



    fun fetchMedia() {
            mediaSessionConnection.subscribe(ROOT_PACKAGE, object :
                MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    _songList.postValue(children)
                }
            })

    }

    fun playMedia(mediaItem: MediaBrowserCompat.MediaItem,isPauseEnable:Boolean=true){

        val nowPlaying=mediaSessionConnection.nowPlaying.value
        val isPrepared=mediaSessionConnection.playbackState.value?.isPrepared ?: false
        val transportControls=mediaSessionConnection.transportControls

        if (isPrepared && mediaItem.mediaId==nowPlaying?.description?.mediaId) {
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
        }
        else
            transportControls.playFromMediaId(mediaItem.mediaId,null)
    }








}