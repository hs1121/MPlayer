package com.example.mplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.TracksAdapter
import com.example.mplayer.Constants.ALBUMS_ROOT
import com.example.mplayer.MainActivity
import com.example.mplayer.databinding.FragmentAlbumBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlbumFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager
    private  lateinit var  mainViewModel: MainViewModel
    lateinit var mAdapter: TracksAdapter
    private lateinit var binding: FragmentAlbumBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainActivity:MainActivity= activity as MainActivity
        mainViewModel= mainActivity.getViewModel()!!
        binding = FragmentAlbumBinding.inflate(layoutInflater)
        if (mainViewModel.albumList.value==null|| mainViewModel.albumList.value?.isHandled() == false)
            // fetch album data if not fetched before or not handled (not updated the list after change in main data)
        mainViewModel.getMedia(ALBUMS_ROOT)
        mAdapter= TracksAdapter(requireContext(),glide,{ item->
            val key= item.description.mediaId!!
            val action=AlbumFragmentDirections.actionToAlbumList(key)
            findNavController().navigate(action)

        },null)
        binding.albumRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            setItemViewCacheSize(16)
           // setHasFixedSize(true)
        }

        mainViewModel.albumList.observe(viewLifecycleOwner,{ list->
            mAdapter.setList(list.peekContent())
        })


        return binding.root
    }
}