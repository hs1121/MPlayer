package com.example.mplayer.Utility

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager

import android.os.Build
import android.util.Log


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

}