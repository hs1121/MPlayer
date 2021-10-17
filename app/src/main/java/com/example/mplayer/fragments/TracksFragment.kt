package com.example.mplayer.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.TracksAdapter
import com.example.mplayer.MainActivity
import com.example.mplayer.PlayerActivity
import com.example.mplayer.R

import com.example.mplayer.viewModels.MainViewModel
import com.example.mplayer.databinding.FragmentTracksBinding
import com.example.mplayer.exoPlayer.SongEntity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton


@AndroidEntryPoint
class TracksFragment : Fragment() {


    @Inject
    lateinit var glide: RequestManager
    private lateinit var mainViewModel:MainViewModel
    private lateinit var binding: FragmentTracksBinding
    lateinit var adapter: TracksAdapter

    var list:MutableList<SongEntity> = mutableListOf()


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        mainViewModel.fetchMedia()
        binding = FragmentTracksBinding.inflate(layoutInflater)
        adapter = TracksAdapter(requireContext(), glide){ clickedItem->
            mainViewModel.playMedia(clickedItem,true)
            startActivity(Intent(requireContext().applicationContext,PlayerActivity::class.java))
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        mainViewModel.songList.observe(viewLifecycleOwner, {
            adapter.setList(it)
        })

        return binding.root
    }





}