package com.example.mplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.TracksAdapter
import com.example.mplayer.MainActivity
import com.example.mplayer.databinding.FragmentAlbumListBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AlbumListFragment : Fragment() {

    private lateinit var binding: FragmentAlbumListBinding
    private lateinit var adapter:TracksAdapter
    private lateinit var mainViewModel:MainViewModel
    val args: AlbumListFragmentArgs by navArgs()

    @Inject
    lateinit var glide:RequestManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainActivity: MainActivity = activity as MainActivity
        mainViewModel= mainActivity.getViewModel()!!
        binding= FragmentAlbumListBinding.inflate(layoutInflater)

        if (mainViewModel.songList.value==null||
            mainViewModel.songList.value?.isHandled()==false) {
            mainViewModel.getMedia(args.mediaId)
        }
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        adapter= TracksAdapter(requireContext(),glide,{
                mainViewModel.itemClicked(it,requireContext())
        },null)
        binding.recyclerView.adapter=adapter
        mainViewModel.songList.observe(viewLifecycleOwner){
            adapter.setList(it.peekContent())

        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity)?.supportActionBar?.title=args.mediaId
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.resetSongList()
        (activity as MainActivity)?.supportActionBar?.title=""

    }

    // todo: issue-> wrong song played on Click



}