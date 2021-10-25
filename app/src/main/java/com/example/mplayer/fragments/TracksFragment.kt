package com.example.mplayer.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.TracksAdapter
import com.example.mplayer.Constants.TRACKS_ROOT
import com.example.mplayer.MainActivity
import com.example.mplayer.PlayerActivity
import com.example.mplayer.viewModels.MainViewModel
import com.example.mplayer.databinding.FragmentTracksBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class TracksFragment : Fragment() {


    @Inject
    lateinit var glide: RequestManager
    private  lateinit var mainViewModel:MainViewModel
    private lateinit var binding: FragmentTracksBinding
     private lateinit var adapter: TracksAdapter




    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val mainActivity: MainActivity = activity as MainActivity
        mainViewModel= mainActivity.getViewModel()!!
        mainViewModel.getMedia(TRACKS_ROOT)
        binding = FragmentTracksBinding.inflate(layoutInflater)
        adapter = TracksAdapter(requireContext(), glide){ clickedItem->
            mainViewModel.playMedia(clickedItem,false)
            startActivity(Intent(requireContext().applicationContext,PlayerActivity::class.java))
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        mainViewModel.tracksList.observe(viewLifecycleOwner, {
            adapter.setList(it)
            Log.d("debug","check")
        })

        return binding.root
    }
    fun getBinding()=binding






}