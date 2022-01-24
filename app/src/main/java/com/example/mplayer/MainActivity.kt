package com.example.mplayer

import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.RequestManager
import com.example.mplayer.Utility.Util
import com.example.mplayer.Utility.isPlaying
import com.example.mplayer.databinding.ActivityMainBinding
import com.example.mplayer.exoPlayer.MusicService
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.http.DELETE
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
     lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var glide:RequestManager



    fun getViewModel()= mainViewModel


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.setStatusBarGradient(this)
      //  window.statusBarColor = getColor(R.color.background_color)
        mainViewModel=MainViewModel.getViewModel(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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


        mainViewModel.initMedia()
        mainViewModel.onControllerReady.observe(this){
            if (it) {
                mainViewModel.initLastPlayedMedia();
                mainViewModel.initPlayModes()
            }

        }


        val playPause: ImageView = findViewById(R.id.play_pause)
        playPause.setOnClickListener { mainViewModel.playPause() }

        mainViewModel.isPlaying.observe(this,{ playbackState ->
            if (playbackState != null) {
                when {
                    playbackState.isPlaying -> {
                        playPause.setImageResource(R.drawable.ic_mini_pause)
                         }
                    else -> playPause.setImageResource(R.drawable.ic_mini_play)
                }
            }
        })

        MusicService.playerInstance.observe(this){ it ->
            if (mainViewModel.exoPlayer==null&&it!=null){
                mainViewModel.exoPlayer=it
                binding.playerMini.player=it
            }else if (mainViewModel.exoPlayer!=null){
                mainViewModel.exoPlayer?.let { binding.playerMini.player=it }
            }
        }

        mainViewModel.currentlyPlayingSong.observe(this,{ item ->
            if(item.description.mediaUri!=null)
                  binding.playerMini.visibility=View.VISIBLE
            val title=findViewById<TextView>(R.id.player_mini_title)
            val image=findViewById<ImageView>(R.id.player_mini_image)
            title.text=item.description.title
            item?.description?.iconUri.let { glide.load(it).into(image) }

        })
        binding.playerMini.setOnClickListener {
//            startActivity(Intent(applicationContext, PlayerActivity::class.java),
//            ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            startActivity(Intent(applicationContext, PlayerActivity::class.java))
        }


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
        else {
            Toast.makeText(this, "Unable to delete", Toast.LENGTH_SHORT).show()
            mainViewModel.mediaRemoved(false)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



}


