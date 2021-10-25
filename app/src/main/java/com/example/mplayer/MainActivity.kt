package com.example.mplayer

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    private lateinit var binding: ActivityMainBinding
    private lateinit var currentState: TabState
    private lateinit var tracksFragment: TracksFragment
    private lateinit var playlistFragment: PlaylistFragment
    private lateinit var albumFragment: AlbumFragment
    private  var extendedFragment: AlbumListFragment?=null
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

            val fromSplashScreen=intent.getBooleanExtra(Constants.FROM_SPLASH_SCREEN,false)

            if (fromSplashScreen&&savedInstanceState==null){

            }else {

                tracksFragment = TracksFragment()
                playlistFragment = PlaylistFragment()
                albumFragment = AlbumFragment()


                binding.bottomNavigation.setOnItemSelectedListener(OnNavigationItemSelectListener())
                binding.bottomNavigation.menu.findItem(R.id.tracks_fragment).isChecked = true
                currentState = TabState.TRACKS
                binding.toolbarLayout.title = getString(R.string.tracks)

                if (savedInstanceState != null) {
                    val count = supportFragmentManager.backStackEntryCount
                    repeat(count) {
                        supportFragmentManager.popBackStack()
                    }
                }
                supportFragmentManager.beginTransaction()
                    .add(R.id.nav_host_container, albumFragment)
                    .add(R.id.nav_host_container, playlistFragment)
                    .add(R.id.nav_host_container, tracksFragment)
                    .commit()


                setTabStateFragment(TabState.TRACKS).commit()

            }


        }

    private fun setTabStateFragment(state: TabState): FragmentTransaction {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val transaction = supportFragmentManager.beginTransaction()
        //  transaction.setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim)
        when (state) {
            TabState.PLAYLISTS -> {
                try {
                    playlistFragment.getBinding().root.visibility = View.VISIBLE
                    tracksFragment.getBinding().root.visibility = View.GONE
                    albumFragment.getBinding().root.visibility = View.GONE
                    extendedFragment?.let { it.getBinding().root.visibility = View.GONE }
                } catch (e: Exception) {
                }

                currentState = TabState.PLAYLISTS
                binding.toolbarLayout.title = getString(R.string.playlists)
            }
            TabState.TRACKS -> {
                try {
                    tracksFragment.getBinding().root.visibility = View.VISIBLE
                    playlistFragment.getBinding().root.visibility = View.GONE
                    albumFragment.getBinding().root.visibility = View.GONE
                    extendedFragment?.let { it.getBinding().root.visibility = View.GONE }
                } catch (e: Exception) {
                }
                currentState = TabState.TRACKS
                binding.toolbarLayout.title = getString(R.string.tracks)
            }
            TabState.ALBUMS -> {
                try {
                    albumFragment.getBinding().root.visibility = View.VISIBLE
                    playlistFragment.getBinding().root.visibility = View.GONE
                    tracksFragment.getBinding().root.visibility = View.GONE
                    extendedFragment?.let { it.getBinding().root.visibility = View.GONE }
                } catch (e: Exception) {
                }
                currentState = TabState.ALBUMS
                binding.toolbarLayout.title = getString(R.string.albums)
            }
            else->{
                try {
                    albumFragment.getBinding().root.visibility = View.GONE
                    playlistFragment.getBinding().root.visibility = View.GONE
                    tracksFragment.getBinding().root.visibility = View.GONE
                    extendedFragment?.let { it.getBinding().root.visibility = View.VISIBLE }
                } catch (e: Exception) {
                }
                currentState=TabState.ALBUM_EXTENDED
            }
        }
        return transaction
    }

    private fun transact(
        transaction: FragmentTransaction,
        fragmentVisible: Fragment,
        fragment2: Fragment,
        fragment3: Fragment
    ) {
        if (!fragmentVisible.isResumed)
            fragmentVisible.onResume()
        if (fragment2.isResumed)
            fragment2.onPause()
        if (fragment3.isResumed)
            fragment3.onPause()
    }


    private inner class OnNavigationItemSelectListener : NavigationBarView.OnItemSelectedListener {
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
        ALBUMS,
        ALBUM_EXTENDED,
        PLAYLIST_EXTENDED
    }

    override fun onBackPressed() {
        if (currentState==TabState.ALBUM_EXTENDED){
            setTabStateFragment(TabState.ALBUMS).commit()
        }
        else if (currentState != TabState.TRACKS) {
            binding.bottomNavigation.menu.findItem(R.id.tracks_fragment).isChecked = true
            setTabStateFragment(TabState.TRACKS).commit()
        } else
            super.onBackPressed()
    }

}