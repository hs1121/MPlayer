package com.example.mplayer.fragments

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.AlbumAdapter
import com.example.mplayer.Adapters.TracksAdapter
import com.example.mplayer.R
import com.example.mplayer.databinding.FragmentAlbumListBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AlbumListFragment(
    private val songList: MutableList<MediaBrowserCompat.MediaItem>,
    private val mainViewModel: MainViewModel,) : Fragment() {

    private lateinit var binding: FragmentAlbumListBinding
    private lateinit var adapter:AlbumAdapter

    @Inject
    lateinit var glide:RequestManager

    private val arr= mutableListOf("aaa","bbbb","cccccc")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAlbumListBinding.inflate(layoutInflater)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        adapter= AlbumAdapter(requireContext(),){
          //  mainViewModel.itemClicked(it)
        }
        adapter.setList(arr)


        return binding.root
    }
    fun getBinding()=binding

}