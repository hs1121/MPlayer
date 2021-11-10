package com.example.mplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.PlaylistAdapter
import com.example.mplayer.Adapters.TracksAdapter
import com.example.mplayer.Constants
import com.example.mplayer.MainActivity
import com.example.mplayer.Utility.Action
import com.example.mplayer.Utility.AddPlaylistDialog
import com.example.mplayer.Utility.Content
import com.example.mplayer.database.Repository
import com.example.mplayer.database.entity.PlaylistEntity
import com.example.mplayer.databinding.FragmentPlaylistBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager
    @Inject
    lateinit var repository: Repository

    private  lateinit var  mainViewModel: MainViewModel
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var mAdapter: PlaylistAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainActivity: MainActivity = activity as MainActivity
        mainViewModel= mainActivity.getViewModel()!!
        binding = FragmentPlaylistBinding.inflate(layoutInflater)

        if (mainViewModel.playlist.value==null|| mainViewModel.playlist.value?.isHandled() == false)
        mainViewModel.getMedia(Constants.PLAYLIST_ROOT)


        mAdapter= PlaylistAdapter(requireContext(),glide,{
            val action=PlaylistFragmentDirections.actionPlaylistFragmentToSelectionFragment(null,null,
                Action.EDIT,Content.PLAYLIST,Constants.PLAYLIST_ROOT,it.mediaId)
            findNavController().navigate(action)
        }
        ){
                val action = PlaylistFragmentDirections.actionPlaylistFragmentToPlaylistItemFragment(
                    it.mediaId.toString()
                )
            findNavController().navigate(action)
        }


        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            setItemViewCacheSize(16)
        }

        mainViewModel.playlist.observe(viewLifecycleOwner){
            mAdapter.setList(it.peekContent())
        }
        binding.addPlaylist.setOnClickListener {
            showDialog()
            }


        return binding.root
    }

    private fun showDialog(){

        activity?.supportFragmentManager?.let {
            AddPlaylistDialog{ name, by ->
                showSelection(name,by)
            }.show(it,"add Playlist")
        }
    }

    private fun showSelection(name: String, by: String) {
       val action = PlaylistFragmentDirections.actionPlaylistFragmentToSelectionFragment(
           name,by,Action.ADD,Content.TRACK,Constants.TRACKS_ROOT,null)
        findNavController().navigate(action)
    }


}