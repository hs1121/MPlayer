package com.example.mplayer.Utility

import android.content.Context
import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import com.example.mplayer.R
import com.example.mplayer.database.BrowsingTree
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import java.lang.Exception
import javax.inject.Inject

const val DATA_NAME="MPlayerDataStore"
const val SORT_BY="SortName"
const val IS_ASC="IsAsc"
const val CURRENT_SONG="CurrentSong"
const val EQUALIZER_DATA_KEY="EqualizerDataKey"

class PreferenceDataStore(private val context: Context) {

    private val gson=Gson()
    private var dataStore: DataStore<Preferences> = context.createDataStore(name = DATA_NAME)

    @Inject
    lateinit var browsingTree: BrowsingTree


    private suspend fun save(key:String,value:String){
        val dataKey = preferencesKey<String>(key)
        dataStore.edit { storeItem->
            storeItem[dataKey]=value
        }
    }
    private suspend fun read(key:String):String?{
        val dataKey = preferencesKey<String>(key)
        val _preference=dataStore.data
        val preference=_preference.first()
        return preference[dataKey]
    }

    suspend fun saveSortData(isChecked:Boolean?,id:Int?){
        if (isChecked!=null)
            save(IS_ASC,booleanToString(isChecked))
        if (id!=null)
            save(SORT_BY,id.toString())
    }

    suspend fun readSotData():SortData{
        val isAsc=stringToBoolean(read(IS_ASC))
        val id=read(SORT_BY)?.toInt()?: R.id.sort_date
        return SortData(isAsc,id)
    }

    suspend fun saveCurrentSongData(mediaId: String, from: String, time: Long){
        val songData=CurrentSongData(mediaId,from,time)
        save(CURRENT_SONG,songDataToString(songData))
    }
    suspend fun readCurrentSongData(): CurrentSongData? {
        val data = read(CURRENT_SONG)
        return stringToSongData(data)
    }

    suspend fun saveEqData(data: EqualizerData){
        save(EQUALIZER_DATA_KEY,EqDataToString(data))
    }
    suspend fun readEqualizerData(): EqualizerData {
        val data= read(EQUALIZER_DATA_KEY)
        return stringToEqData(data)
    }





    /////////// Type Converters ////////////////////

    private val TRUE="true"
    private val FALSE="false"

    private fun booleanToString(boolean: Boolean?):String{
        return   if (boolean == true) TRUE else FALSE
    }
    private fun stringToBoolean(string: String?):Boolean{
        return string== TRUE
    }

    private fun songDataToString(data:CurrentSongData)= gson.toJson(data)
    private fun stringToSongData(string: String?): CurrentSongData? {
        val data= try {
            string?.let {
                gson.fromJson(it, CurrentSongData::class.java) }
        }catch (e:Exception){
            Log.e("conversion failed",e.toString()+" unable to convert string to song object")
            null
        }
        return data

    }
    private fun EqDataToString(data:EqualizerData)=gson.toJson(data)
    private fun stringToEqData(string: String?):EqualizerData{
        return try {
            gson.fromJson(string,EqualizerData::class.java)
        }
        catch (e :Exception){
            Log.e("conversion failed",e.toString()+" unable to convert string to equalizer object")
            EqualizerData(false,-1, HashMap(),0,0)
        }
    }

}

data class SortData(var isChecked:Boolean, var id:Int)
data class EqualizerData(var isEnable:Boolean,var presetPos:Int,
                         var bands:HashMap<Short,Short>,var bass:Short,var reverb:Short)
data class CurrentSongData(val mediaId:String?,val from:String?,val time:Long?)





