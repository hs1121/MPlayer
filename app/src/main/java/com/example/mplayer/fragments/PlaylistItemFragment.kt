package com.example.mplayer.fragments

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.PlaylistAdapter
import com.example.mplayer.Constants
import com.example.mplayer.MainActivity
import com.example.mplayer.R
import com.example.mplayer.Utility.Action
import com.example.mplayer.Utility.Content
import com.example.mplayer.Utility.DragAndDropHelper
import com.example.mplayer.Utility.from
import com.example.mplayer.databinding.FragmentPlaylistItemBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistItemFragment : Fragment() , PlaylistAdapter.PlaylistItemListener {

    private lateinit var binding:FragmentPlaylistItemBinding
    private lateinit var mAdapter: PlaylistAdapter
    @Inject
    lateinit var glide:RequestManager
    private lateinit var mainViewModel: MainViewModel

    private val args:PlaylistItemFragmentArgs by navArgs()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.supportActionBar?.title=args.mediaId
        mainActivity.supportActionBar?.title=""

        mainViewModel= mainActivity.getViewModel()!!
        binding= FragmentPlaylistItemBinding.inflate(inflater)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())

        mAdapter= PlaylistAdapter(requireContext(),glide)
        mAdapter.setPlaylistItemListener(this)
        binding.recyclerView.adapter=mAdapter




        if (mainViewModel.songList.value==null||
            mainViewModel.songList.value?.isHandled()==false)
        mainViewModel.getMedia(args.mediaId)

        mainViewModel.songList.observe(viewLifecycleOwner){
            mAdapter.setList(it.data)
        }
        setHasOptionsMenu(true)

        if(mAdapter!=null) test(0) else test(1)
        test(0)
        return binding.root

    }

    private fun test(value:Int=0, str:String="")="hello"

    override fun onResume() {
        super.onResume()
        (activity as MainActivity)?.supportActionBar?.title=args.mediaId
    }


    override fun onDestroyView() {

        super.onDestroyView()
        mainViewModel.resetSongList()
        (activity as MainActivity)?.supportActionBar?.title=""

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val item = menu.findItem(R.id.sort_icon)
        item.isVisible = false

        inflater.inflate(R.menu.arrange_menu_item,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId){
            R.id.arrange_items->{
                Toast.makeText(requireContext(), "Hold and Drag to move", Toast.LENGTH_LONG).show()
               val action = PlaylistItemFragmentDirections.actionPlaylistItemFragmentToArrangeFragment(args.mediaId)
                findNavController().navigate(action)
                true
            }
            R.id.add_items->{
                showSelection(args.mediaId,"")
                true
            }
            else->{  super.onOptionsItemSelected(item)}
        }
    }
    private fun showSelection(name: String, by: String) {
        val action = PlaylistItemFragmentDirections.actionPlaylistItemFragmentToSelectionFragment(
            name,by,Action.ADD,Content.TRACK,Constants.TRACKS_ROOT,null,0)
        findNavController().navigate(action)
    }

    override fun onItemClick(item: MediaBrowserCompat.MediaItem) {
        mainViewModel.itemClicked(item,requireContext())
    }

    override fun onLongClick(item: MediaBrowserCompat.MediaItem,position: Int) {
        val action=PlaylistItemFragmentDirections.actionPlaylistItemFragmentToSelectionFragment(null,null,
            Action.EDIT,
            Content.PLAYLIST_ITEM,
            item.from,item.mediaId,position)
        findNavController().navigate(action)
    }

    override fun onItemEdited(id:String,name: String, by: String, item: MediaBrowserCompat.MediaItem) {
        TODO("Not yet implemented")
    }

    override fun onItemDelete(item: MediaBrowserCompat.MediaItem) {
        TODO("Not yet implemented")
    }

}