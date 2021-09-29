package com.example.mplayer

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorDestinationBuilder
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.mplayer.Constants.PERMISSION_READ_INTERNAL_STORAGE
import com.example.mplayer.Utility.FragmentTransection
import com.example.mplayer.viewModels.MainViewModel
import com.example.mplayer.databinding.ActivityMainBinding
import com.example.mplayer.fragments.AlbumFragment
import com.example.mplayer.fragments.PlaylistFragment
import com.example.mplayer.fragments.TracksFragment
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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


        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            if (it.itemId == R.id.tracks_fragment) {
                navController.popBackStack(R.id.tracks_fragment, false)
                true
            }
            else
                NavigationUI.onNavDestinationSelected(it , navController)
        }

    }

    override fun onSupportNavigateUp(): Boolean { // use ?
        return navController.navigateUp() || return super.onSupportNavigateUp()
    }
}