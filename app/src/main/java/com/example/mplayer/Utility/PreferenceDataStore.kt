package com.example.mplayer.Utility

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import com.example.mplayer.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

const val DATA_NAME="MPlayerDataStore"
const val SORT_BY="SortName"
const val IS_ASC="IsAsc"

class PreferenceDataStore(private val context: Context) {

    private var dataStore: DataStore<Preferences> = context.createDataStore(name = DATA_NAME)



    private suspend fun save(key:String,value:String){
        val dataKey = preferencesKey<String>(key)
        dataStore.edit { storeItem->
            storeItem[dataKey]=value
        }
    }
    private suspend fun read(key:String):String?{
        val dataKey = preferencesKey<String>(key)
        val preference=dataStore.data.first()
        return preference[dataKey]
    }

    suspend fun saveSortData(isChecked:Boolean?,id:Int?){
        if (isChecked!=null)
            save(IS_ASC,booleanToString(isChecked))
        if (id!=null)
            save(SORT_BY,id.toString())
    }

    suspend fun getSotData():SortData{
        val isAsc=stringToBoolean(read(IS_ASC))
        val id=read(SORT_BY)?.toInt()?: R.id.sort_date
        return SortData(isAsc,id)
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

}

data class SortData(val isChecked:Boolean,val id:Int)





