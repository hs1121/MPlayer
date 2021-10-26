package com.example.mplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.AlbumAdapter
import com.example.mplayer.Adapters.TracksAdapter
import com.example.mplayer.Constants.ALBUMS_ROOT
import com.example.mplayer.MainActivity
import com.example.mplayer.R
import com.example.mplayer.databinding.FragmentAlbumBinding
import com.example.mplayer.databinding.FragmentPlaylistBinding
import com.example.mplayer.databinding.FragmentTracksBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlbumFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager
    private  lateinit var  mainViewModel: MainViewModel
    lateinit var adapter: TracksAdapter
    private lateinit var binding: FragmentAlbumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainActivity:MainActivity= activity as MainActivity
        mainViewModel= mainActivity.getViewModel()!!
        binding = FragmentAlbumBinding.inflate(layoutInflater)
        if (mainViewModel.albumList.value==null|| mainViewModel.albumList.value?.isHandled() == false)
        mainViewModel.getMedia(ALBUMS_ROOT)
        adapter= TracksAdapter(requireContext(),glide){item->
            val key= item.description.mediaId!!
            val action=AlbumFragmentDirections.actionToAlbumList(key)
            findNavController().navigate(action)

        }

        mainViewModel.albumList.observe(viewLifecycleOwner,{ list->
            adapter.setList(list.peekContent())
        })

        binding.albumRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.albumRecyclerView.adapter=adapter
        binding

        return binding.root
    }
    fun getBinding()=binding
}