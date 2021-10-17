package com.example.mplayer

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.mplayer.databinding.ActivityMainBinding
import com.example.mplayer.fragments.AlbumFragment
import com.example.mplayer.fragments.PlaylistFragment
import com.example.mplayer.fragments.TracksFragment
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var currentState:TabState
    private lateinit var tracksFragment: TracksFragment
    private lateinit var playlistFragment: PlaylistFragment
    private lateinit var albumFragment: AlbumFragment


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor=getColor(R.color.background_color)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        tracksFragment=TracksFragment()
        playlistFragment=PlaylistFragment()
        albumFragment=AlbumFragment()


        binding.bottomNavigation.setOnItemSelectedListener (OnNavigationItemSelectListener())
        binding.bottomNavigation.menu.findItem(R.id.tracks_fragment).isChecked = true
        currentState= TabState.TRACKS
        binding.toolbarLayout.title=getString(R.string.tracks)

        supportFragmentManager.beginTransaction()
            .add(R.id.nav_host_container, playlistFragment)
            .add(R.id.nav_host_container, tracksFragment)
            .add(R.id.nav_host_container, albumFragment)
            .commit()
        setTabStateFragment(TabState.TRACKS).commit()




    }
    private fun setTabStateFragment(state: TabState): FragmentTransaction {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim)
        when (state) {
            TabState.PLAYLISTS -> {
                transaction.show(playlistFragment)
                transaction.hide(tracksFragment)
                transaction.hide(albumFragment)
                currentState=TabState.PLAYLISTS
                binding.toolbarLayout.title=getString(R.string.playlists)
            }
            TabState.TRACKS -> {
                transaction.show(tracksFragment)
                transaction.hide(playlistFragment)
                transaction.hide(albumFragment)
                currentState=TabState.TRACKS
                binding.toolbarLayout.title=getString(R.string.tracks)
            }
            TabState.ALBUMS -> {
                transaction.show(albumFragment)
                transaction.hide(playlistFragment)
                transaction.hide(tracksFragment)
                currentState=TabState.ALBUMS
                binding.toolbarLayout.title=getString(R.string.albums)
            }
        }
        return transaction
    }



    private inner class OnNavigationItemSelectListener: NavigationBarView.OnItemSelectedListener{
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.playlist_fragment -> {
                    setTabStateFragment(TabState.PLAYLISTS).commit()
                     true
                }
                R.id.tracks_fragment -> {
                    setTabStateFragment(TabState.TRACKS).commit()
                     true
                }
                R.id.album_fragment -> {
                    setTabStateFragment(TabState.ALBUMS).commit()
                     true
                }
                else -> false
            }
        }
    }

    internal enum class TabState {
        PLAYLISTS,
        TRACKS,
        ALBUMS
    }

    override fun onBackPressed() {
        if (currentState!=TabState.TRACKS){
            binding.bottomNavigation.menu.findItem(R.id.tracks_fragment).isChecked = true
            setTabStateFragment(TabState.TRACKS).commit()
        }
        else
        super.onBackPressed()
    }

}