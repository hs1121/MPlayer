package com.example.mplayer

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mplayer.Constants.PERMISSION_READ_INTERNAL_STORAGE
import com.example.mplayer.Utility.Util
import com.example.mplayer.ViewModels.MainViewModel
import com.example.mplayer.databinding.ActivityMainBinding
import com.example.mplayer.exoPlayer.MusicSource
import com.example.mplayer.exoPlayer.SongEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var mainViewModel: MainViewModel

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        mainViewModel= MainViewModel.getViewModel(this)
        if(savedInstanceState == null) {
            if (Util.isStoragePermissionGranted(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    PERMISSION_READ_INTERNAL_STORAGE
                )
            ) {
                mainViewModel.fetchMedia()
            }
        }
        navController=findNavController(R.id.nav_host_container)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.playlist_fragment,
                R.id.tracks_fragment,
                R.id.album_fragment
            )
        )
        binding.bottomNavigation.setupWithNavController(navController)
        binding.toolbarLayout.setupWithNavController(binding.toolbar,navController,appBarConfiguration)

        Log.d("this","that")


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
       if (requestCode== PERMISSION_READ_INTERNAL_STORAGE&& permissions.isNotEmpty() &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
           mainViewModel.fetchMedia()
       }
        else
            finish()
    }

    override fun onSupportNavigateUp(): Boolean { // use ?
        return navController.navigateUp() || return super.onSupportNavigateUp()
    }
}