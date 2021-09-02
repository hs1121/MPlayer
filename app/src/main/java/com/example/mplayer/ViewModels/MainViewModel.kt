package com.example.mplayer.ViewModels

import android.app.Application
import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.*
import com.example.mplayer.exoPlayer.MusicSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
     private val musicSource: MusicSource,
    application:Application
): AndroidViewModel(application) {

    private var _songList= MutableLiveData<MutableList<MediaMetadataCompat>>()
    var songList:LiveData<MutableList<MediaMetadataCompat>> = _songList


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
        viewModelScope.launch {
            musicSource.getSongs()
        }
        musicSource.whenReady {
            _songList.postValue(musicSource.musicItems)
        }
    }


}