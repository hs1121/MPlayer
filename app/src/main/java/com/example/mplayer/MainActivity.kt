package com.example.mplayer

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import retrofit2.http.DELETE
import java.lang.Exception

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
     lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel




    fun getViewModel()= mainViewModel


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel=ViewModelProvider(this)
            .get(MainViewModel::class.java)

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

            binding.bottomNavigation.selectedItemId=R.id.tracks_fragment

        }
        mainViewModel?.initMedia()

    }





    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_app_bar_menu,menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean { // use ?
        return navController.navigateUp() || return super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode==Constants.REQUEST_CODE_DELETE&& resultCode== AppCompatActivity.RESULT_OK) {
            mainViewModel.mediaRemoved(true)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}


