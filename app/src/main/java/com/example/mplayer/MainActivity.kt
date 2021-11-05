package com.example.mplayer

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.mplayer.databinding.ActivityMainBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
     lateinit var binding: ActivityMainBinding
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

        if (true ) {
            navController = findNavController(R.id.nav_host_container)
            setSupportActionBar(binding.toolbar)

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

            binding.bottomNavigation.setOnItemSelectedListener {
                if (it.itemId == R.id.tracks_fragment) {
                    navController.popBackStack(R.id.tracks_fragment, false)
                    true
                }
                else
                    NavigationUI.onNavDestinationSelected(it , navController)
            }
            binding.bottomNavigation.selectedItemId=R.id.tracks_fragment

        }


    }

    fun setAppBar(id:Int){
        binding.toolbar.inflateMenu(id)

        binding.toolbar.setOnMenuItemClickListener { item->
            return@setOnMenuItemClickListener when(item.itemId){
                 R.id.confirm_button->{
                                true
                            }
                else-> false
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_app_bar_menu,menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean { // use ?
        return navController.navigateUp() || return super.onSupportNavigateUp()
    }
}


