package com.example.mplayer.fragments

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("tracks","OnCreate")
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        mainViewModel.fetchMedia()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTracksBinding.inflate(layoutInflater)

        adapter = TracksAdapter(requireContext(), glide)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        mainViewModel.songList.observe(viewLifecycleOwner, {
            adapter.setList(it)
        })

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(activity, "destroyed", Toast.LENGTH_SHORT).show()
    }



}