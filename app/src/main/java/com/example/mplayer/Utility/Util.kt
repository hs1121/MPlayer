package com.example.mplayer.Utility

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.net.Uri

import android.os.Build
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.example.mplayer.Constants
import java.lang.Exception
import android.widget.Toast


object Util {

    fun isStoragePermissionGranted(
        context: Activity,
        permission: String,
        requestCode: Int
    ): Boolean {
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


    fun requestDeletePermission(
        context: Activity,
        uriList: List<Uri>,
        callback: (Boolean, String) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val pi: PendingIntent = MediaStore.createDeleteRequest(context.contentResolver, uriList)
            try {

                context.startIntentSenderForResult(
                    pi.intentSender, Constants.REQUEST_CODE_DELETE,
                    Intent(), 0, 0, 0
                )
            } catch (e: Exception) {
            }
        } else
            deleteFileUsingDisplayName(context, uriList, callback)
    }

    private fun deleteFileUsingDisplayName(
        activity: Activity,
        uriList: List<Uri>,
        callback: (Boolean, String) -> Unit
    ) {


        val resolver = activity.contentResolver
        try {
            var successes = 0
            uriList.forEach { uri ->
                val result = resolver.delete(uri, null, null)
                Log.d("resultCode", result.toString())
                if (result > 0)
                    successes++
            }
            if (successes == uriList.size)
                callback(true, "")
            else if (successes > 0 && successes < uriList.size)
                callback(false, "Unable to delete few files")
            else
                callback(false, "Unable to delete")
        } catch (ex: Exception) {
            ex.printStackTrace()
            callback(false, "Unable to delete")
        }
    }

    fun asMediaItem(song: MediaMetadataCompat): MediaBrowserCompat.MediaItem{
        val desc = MediaDescriptionCompat.Builder()
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .setMediaUri(song.description.mediaUri)
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
        desc.from = song.from

        return   MediaBrowserCompat.MediaItem(
            desc.build(),
            song.bundle.getLong(Constants.METADATA_KEY_FLAG).toInt())
    }

}

enum class Action {
    EDIT,
    ADD
}

enum class Content {
    PLAYLIST,
    PLAYLIST_ITEM,
    TRACK
}