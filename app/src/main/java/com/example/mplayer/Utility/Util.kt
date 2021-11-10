package com.example.mplayer.Utility

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.net.Uri

import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.example.mplayer.Constants
import com.example.mplayer.MainActivity
import java.lang.Exception
import java.util.ArrayList


object  Util {

        fun isStoragePermissionGranted(context: Activity,permission:String,requestCode:Int): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                    true
                } else {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(permission),
                        requestCode
                    )
                    false
                }
            } else { //permission is automatically granted on sdk<23 upon installation
                true
            }
        }

    fun requestDeletePermission( context:Activity,uriList:List<Uri>){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val pi: PendingIntent = MediaStore.createDeleteRequest(context.contentResolver, uriList)
            try {

                context.startIntentSenderForResult(pi.intentSender,Constants.REQUEST_CODE_DELETE,
                    Intent(),0,0,0)
            } catch (e: Exception) { }
        }
    }




}

enum class Action {
    EDIT,
    ADD
}

enum class Content{
    PLAYLIST,
    PLAYLIST_ITEM,
    TRACK
}