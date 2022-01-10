package com.example.mplayer.Utility

import com.example.mplayer.database.Repository
import com.google.android.exoplayer2.C
import javax.inject.Inject

class NameValidator @Inject constructor( private val repository:Repository) {


    fun isPlaylistNameValid(name:String?,currentName:String?):DataResults<String>{
        return if (name.isNullOrBlank()){
            DataResults.Error("Invalid Name")
        }
        else if (name==currentName)
            DataResults.Loading()
        else if (!repository.browsingTree.isRootAvailable(name)){
            DataResults.Error("Name Already Exists")
        }
        else
            DataResults.Success(name.trim())
    }
}