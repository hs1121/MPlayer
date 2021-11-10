package com.example.mplayer.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.PlaylistAdapter
import com.example.mplayer.MainActivity
import com.example.mplayer.R
import com.example.mplayer.Utility.DragAndDropHelper
import com.example.mplayer.database.entity.PlaylistEntity
import com.example.mplayer.databinding.FragmentArrangeBinding
import com.example.mplayer.databinding.FragmentPlaylistItemBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArrangeFragment : Fragment() {

    private lateinit var binding: FragmentArrangeBinding
    private lateinit var mAdapter: PlaylistAdapter
    @Inject
    lateinit var glide: RequestManager
    private lateinit var mainViewModel: MainViewModel

    private val args:ArrangeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mainActivity: MainActivity = activity as MainActivity
        mainViewModel= mainActivity.getViewModel()!!
        binding = FragmentArrangeBinding.inflate(inflater)
        mainViewModel= mainActivity.getViewModel()!!
        binding.recyclerView.layoutManager= LinearLayoutManager(requireContext())
        mAdapter= PlaylistAdapter(requireContext(),glide,{}){
        }
        binding.recyclerView.adapter=mAdapter

        val dragAndDropHelper= ItemTouchHelper(DragAndDropHelper(mAdapter))
        dragAndDropHelper.attachToRecyclerView(binding.recyclerView)


        if (mainViewModel.songList.value==null||
            mainViewModel.songList.value?.isHandled()==false)
            mainViewModel.getMedia(args.mediaId)

        mainViewModel.songList.observe(viewLifecycleOwner){
            mAdapter.setList(it.data)
        }
        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.selection_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.confirm_button -> {
                val list = mAdapter.getList().map { it.mediaId }.toMutableList()
                mainViewModel.updatePlaylistItem(list, args.mediaId){
                    mainViewModel.resetPlaylist()
                    activity?.onBackPressed()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)

        }
    }


}