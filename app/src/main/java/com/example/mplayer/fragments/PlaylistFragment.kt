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
import com.example.mplayer.MainActivity
import com.example.mplayer.Utility.AddPlaylistDialog
import com.example.mplayer.database.entity.PlaylistEntity
import com.example.mplayer.databinding.FragmentPlaylistBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private  lateinit var  mainViewModel: MainViewModel
    private lateinit var binding: FragmentPlaylistBinding
    private  lateinit var playlist : MutableList<PlaylistEntity>
    private lateinit var mAdapter: PlaylistAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainActivity: MainActivity = activity as MainActivity
        mainViewModel= mainActivity.getViewModel()!!
        binding = FragmentPlaylistBinding.inflate(layoutInflater)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        mAdapter= PlaylistAdapter(requireContext(),glide){
                val action = PlaylistFragmentDirections.actionPlaylistFragmentToPlaylistItemFragment(
                    it.data.toTypedArray()
                )
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter=mAdapter

        mainViewModel.playlist.observe(viewLifecycleOwner){list->
            playlist=list?: mutableListOf()
            mAdapter.setList(playlist)

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
       val action = PlaylistFragmentDirections.actionPlaylistFragmentToSelectionFragment(name,by)
        findNavController().navigate(action)
    }

}