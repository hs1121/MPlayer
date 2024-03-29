package com.example.mplayer.fragments

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.PlaylistAdapter

import com.example.mplayer.Constants
import com.example.mplayer.MainActivity
import com.example.mplayer.R
import com.example.mplayer.Utility.Action
import com.example.mplayer.Utility.AddPlaylistDialog
import com.example.mplayer.Utility.Content
import com.example.mplayer.database.Repository
import com.example.mplayer.database.entity.PlaylistEntity
import com.example.mplayer.databinding.FragmentPlaylistBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistFragment : Fragment() , PlaylistAdapter.PlaylistItemListener {

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
        mainViewModel= mainActivity.getViewModel()
        binding = FragmentPlaylistBinding.inflate(layoutInflater)

        if (mainViewModel.playlist.value==null|| mainViewModel.playlist.value?.isHandled() == false)
        mainViewModel.getMedia(Constants.PLAYLIST_ROOT)



        mAdapter= PlaylistAdapter(requireContext(),glide)
        mAdapter.setPlaylistItemListener(this)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            setItemViewCacheSize(16)
        }

        mainViewModel.playlist.observe(viewLifecycleOwner){
            if (it.peekContent().isEmpty()){
                binding.nestedScrollView.visibility=View.GONE
                binding.noSongText.visibility=View.VISIBLE
            }
            else {
                binding.nestedScrollView.visibility=View.VISIBLE
                binding.noSongText.visibility=View.GONE
                mAdapter.setList(it.peekContent())
            }
        }



        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
       val addIcon= menu.findItem(R.id.add_icon)
        addIcon.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
      return  when(item.itemId){
            R.id.add_icon->{
                showDialog()
                true
            }
            else ->super.onOptionsItemSelected(item)
        }

    }

    private fun showDialog(){

        activity?.supportFragmentManager?.let {
            AddPlaylistDialog(null,null){ name, by ->
                showSelection(name,by)
            }.show(it,"add Playlist")
        }
    }

    private fun showSelection(name: String, by: String) {
       val action = PlaylistFragmentDirections.actionPlaylistFragmentToSelectionFragment(
           name,by,Action.ADD,Content.TRACK,Constants.TRACKS_ROOT,null,0)
        findNavController().navigate(action)
    }


    override fun onItemClick(item: MediaBrowserCompat.MediaItem) {
        val action = PlaylistFragmentDirections.actionPlaylistFragmentToPlaylistItemFragment(
            item.mediaId.toString(),item.description.title.toString()
        )
        findNavController().navigate(action)
    }

    override fun onLongClick(item: MediaBrowserCompat.MediaItem,position: Int) {
        val action=PlaylistFragmentDirections.actionPlaylistFragmentToSelectionFragment(null,null,
            Action.EDIT,Content.PLAYLIST,Constants.PLAYLIST_ROOT,item.mediaId,position)
        findNavController().navigate(action)
    }

    override fun onItemEdited(id:String,name: String, by: String, item: MediaBrowserCompat.MediaItem) {
        lifecycleScope.launch(Dispatchers.IO){
           val entity= mainViewModel.getPlayList(name)
            entity?.name=name
            entity?.createdBy=by
            if (entity != null) {
                mainViewModel.updatePlaylist(entity){

                }
            }
        }
    }

    override fun onItemDelete(item: MediaBrowserCompat.MediaItem) {
      val list= mutableListOf<String>().apply { add(item.mediaId!!) }
        mainViewModel.deletePlaylists(list){
            Toast.makeText(requireContext(), "Playlist Deleted", Toast.LENGTH_SHORT).show()
        }
    }


    override fun getFragmentManagerInstance(): FragmentManager? {
         var manager:FragmentManager?=null
         activity?.supportFragmentManager?.let {
         manager=it
         }
         return manager
    }



}