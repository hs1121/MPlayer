package com.example.mplayer.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.SelectionAdapter
import com.example.mplayer.Constants
import com.example.mplayer.MainActivity
import com.example.mplayer.R
import com.example.mplayer.Utility.Action
import com.example.mplayer.Utility.Content
import com.example.mplayer.Utility.Util
import com.example.mplayer.database.entity.PlaylistEntity
import com.example.mplayer.databinding.FragmentSelectionBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectionFragment : Fragment() {

    private lateinit var binding: FragmentSelectionBinding
    private lateinit var mAdapter: SelectionAdapter

    private lateinit var mainViewModel: MainViewModel

    val args: SelectionFragmentArgs by navArgs()

    @Inject
    lateinit var glide: RequestManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainActivity: MainActivity = activity as MainActivity
        mainViewModel = mainActivity.getViewModel()!!
        binding = FragmentSelectionBinding.inflate(inflater)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = SelectionAdapter(requireContext(), glide, args.selectedItemId)
        binding.recyclerView.adapter = mAdapter
        when (args.mediaId) {
            Constants.TRACKS_ROOT -> {
                mainViewModel.tracksList.value?.let { event ->
                    event.data.let { mAdapter.setList(it) }
                }
            }
            Constants.ALBUMS_ROOT -> {
                mainViewModel.albumList.value?.let { event ->
                    event.data.let { mAdapter.setList(it) }
                }
            }
            Constants.PLAYLIST_ROOT -> {
                mainViewModel.playlist.value?.let { event ->
                    event.data.let { mAdapter.setList(it) }
                }
            }
            else -> {
                mainViewModel.songList.value?.let { event ->
                    event.data.let { mAdapter.setList(it) }
                }
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.selection_menu, menu)
        when (args.action) {
            Action.ADD -> {
                val item = menu.findItem(R.id.delete_button)
                item.isVisible = false
            }
            Action.EDIT -> {
                val item = menu.findItem(R.id.confirm_button)
                item.isVisible = false
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.confirm_button -> {

                val list = mAdapter.getSelectedItems()
                val name = args.name!!
                val by = args.by?:""

                val iconUri: String? = if (list.isNotEmpty()) {
                    list[0].description.iconUri?.toString()
                } else {
                    ""
                }
                val idList = mutableListOf<String?>()
                list.forEach { idList.add(it.description.mediaId) }

                val playlistEntity = PlaylistEntity(name, by, "", iconUri, idList)

                mainViewModel.insertPlaylist(playlistEntity) {
                    activity?.onBackPressed()
                }


                true
            }

            R.id.delete_button -> {
                performDelete()
                true
            }

            else -> super.onOptionsItemSelected(item)

        }


    }

    private fun performDelete() {
        when (args.content) {
            Content.PLAYLIST -> {
                mainViewModel.deletePlaylists(mAdapter.getSelectedItems().map { it.mediaId!! }.toMutableList()) {
                    activity?.onBackPressed()
                }
            }
            Content.PLAYLIST_ITEM -> {
                val updatedList = mAdapter.getUnSelectedItems().map { it.mediaId }.toMutableList()
                mainViewModel.updatePlaylistItem(updatedList, args.mediaId) {
                    activity?.onBackPressed()
                }
            }
            else -> {
                activity?.let { Util.requestDeletePermission(it,mAdapter.getUris()){success,message->
                    if (success)
                        mainViewModel.mediaRemoved(true)
                    else {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        mainViewModel.mediaRemoved(false)
                    }
                }
                 }
            }
        }
        mainViewModel.mediaRemoved.observe(viewLifecycleOwner){
            if (it) {
                mainViewModel.mediaRemoved(mAdapter.getUris().toMutableList()) {
                    Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT)
                        .show()
                    activity?.onBackPressed()
                }

            }
            else
                activity?.onBackPressed()

        }
    }

   


}

