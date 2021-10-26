package com.example.mplayer

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mplayer.databinding.ActivityMainBinding
import com.example.mplayer.exoPlayer.MusicSource
import com.example.mplayer.fragments.AlbumFragment
import com.example.mplayer.fragments.AlbumListFragment
import com.example.mplayer.fragments.PlaylistFragment
import com.example.mplayer.fragments.TracksFragment
import com.example.mplayer.viewModels.MainViewModel
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var mainViewModel: MainViewModel?=null




    fun getViewModel()= if (mainViewModel==null) ViewModelProvider(this)
        .get(MainViewModel::class.java).also { mainViewModel=it ; setViewModelListeners()}
    else mainViewModel

    private fun setViewModelListeners() {
        mainViewModel?.songList?.observe(this){ list->
            if (list!=null){
//                val transaction = supportFragmentManager.beginTransaction()
//                extendedFragment=AlbumListFragment(list, mainViewModel!!)
//                transaction.add(R.id.nav_host_container,extendedFragment!!).commit()
//                setTabStateFragment(TabState.ALBUM_EXTENDED).commit()


            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = getColor(R.color.background_color)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val fromSplashScreen = intent.getBooleanExtra(Constants.FROM_SPLASH_SCREEN, false)

        if (fromSplashScreen && savedInstanceState != null) {
            navController = findNavController(R.id.nav_host_container)


            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.playlist_fragment,
                    R.id.tracks_fragment,
                    R.id.album_fragment
                )
            )

            binding.bottomNavigation.setupWithNavController(navController)
            binding.toolbarLayout.setupWithNavController(
                binding.toolbar,
                navController,
                appBarConfiguration
            )


            Log.d("this", "that")


        }
        // todo: add code to get trackFragment from stack (from older commit in github)
    }

    override fun onSupportNavigateUp(): Boolean { // use ?
        return navController.navigateUp() || return super.onSupportNavigateUp()
    }
}