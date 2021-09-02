package com.example.mplayer.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.TracksAdapter

import com.example.mplayer.Constants.PERMISSION_READ_INTERNAL_STORAGE
import com.example.mplayer.Utility.Util
import com.example.mplayer.ViewModels.MainViewModel
import com.example.mplayer.databinding.FragmentTracksBinding
import com.example.mplayer.exoPlayer.MusicSource
import com.example.mplayer.exoPlayer.SongEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        binding= FragmentTracksBinding.inflate(layoutInflater)
        mainViewModel= MainViewModel.getViewModel(requireActivity())

        adapter= TracksAdapter(requireContext(),glide)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerView.adapter=adapter

        mainViewModel.songList.observe(viewLifecycleOwner,{
            adapter.setList(it)
        })






        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setList() {


    }

}