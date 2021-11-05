package com.example.mplayer.fragments

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.TracksAdapter
import com.example.mplayer.MainActivity
import com.example.mplayer.R
import com.example.mplayer.databinding.FragmentPlaylistItemBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistItemFragment : Fragment() {

    private lateinit var binding:FragmentPlaylistItemBinding
    private lateinit var mAdapter:TracksAdapter
    @Inject
    lateinit var glide:RequestManager
    private lateinit var mainViewModel: MainViewModel

    private val args:PlaylistItemFragmentArgs by navArgs()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainActivity: MainActivity = activity as MainActivity
        mainViewModel= mainActivity.getViewModel()!!
        binding= FragmentPlaylistItemBinding.inflate(inflater)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        mAdapter= TracksAdapter(requireContext(),glide){
            mainViewModel.itemClicked(it,requireContext())
        }
        binding.recyclerView.adapter=mAdapter

        mainViewModel.getMedia(args.mediaId)

        mainViewModel.songList.observe(viewLifecycleOwner){
            mAdapter.setList(it.data)
        }


        return binding.root
    }


}