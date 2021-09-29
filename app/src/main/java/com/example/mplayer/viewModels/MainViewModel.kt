package com.example.mplayer.viewModels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.*
import com.example.mplayer.Constants.ROOT_PACKAGE
import com.example.mplayer.exoPlayer.MediaSessionConnection
import com.example.mplayer.exoPlayer.MusicSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
     private val musicSource: MusicSource,
     private val mediaSessionConnection: MediaSessionConnection
): ViewModel() {

    var alreadyFetched:Boolean=false
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
        if (!alreadyFetched) {
            alreadyFetched=true
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
    }








}