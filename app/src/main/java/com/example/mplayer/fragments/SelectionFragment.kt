package com.example.mplayer.fragments

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.SelectionAdapter
import com.example.mplayer.MainActivity
import com.example.mplayer.R
import com.example.mplayer.database.entity.PlaylistEntity
import com.example.mplayer.databinding.FragmentSelectionBinding
import com.example.mplayer.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectionFragment : Fragment() {

    private lateinit var binding: FragmentSelectionBinding
    private lateinit var mAdapter: SelectionAdapter
    private lateinit var  listener:OnItemSelected
    private lateinit var mainViewModel: MainViewModel

    val args: SelectionFragmentArgs by navArgs()

    @Inject
    lateinit var glide:RequestManager




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainActivity: MainActivity = activity as MainActivity
        mainViewModel= mainActivity.getViewModel()!!
        binding = FragmentSelectionBinding.inflate(inflater)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = SelectionAdapter(requireContext(), glide)
        binding.recyclerView.adapter = mAdapter
        mainViewModel.tracksList.value?.let { event ->
            event.data.let { mAdapter.setList(it) }
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

                val list = mAdapter.getSelectedItems()
                val name = args.name
                val by = args.by

                var iconUri :String? = null

                iconUri = if (list.isNotEmpty()){
                    list[0].description.iconUri?.toString()
                }else{
                    ""
                }
                val idList= mutableListOf<String?>()
                list.forEach { idList.add(it.description.mediaId) }

                val playlistEntity = PlaylistEntity(name,by,"",iconUri,idList)

                mainViewModel.insertPlaylist(playlistEntity)
                mainViewModel.resetPlaylist()
                activity?.onBackPressed().also { return true }
                true
            }

            else -> super.onOptionsItemSelected(item)

        }


    }

     interface OnItemSelected{
        fun itemSelected(list:MutableList<MediaBrowserCompat.MediaItem>)
    }

    fun setOnItemSelectListener(listener:OnItemSelected){
        this.listener=listener
    }

}

