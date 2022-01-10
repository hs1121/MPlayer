package com.example.mplayer.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.TracksAdapter
import com.example.mplayer.Constants.TRACKS_ROOT
import com.example.mplayer.MainActivity
import com.example.mplayer.R
import com.example.mplayer.Utility.Action
import com.example.mplayer.Utility.Content
import com.example.mplayer.Utility.from
import com.example.mplayer.viewModels.MainViewModel
import com.example.mplayer.databinding.FragmentTracksBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class TracksFragment : Fragment() {


    @Inject
    lateinit var glide: RequestManager
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentTracksBinding
    private lateinit var mAdapter: TracksAdapter


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val mainActivity: MainActivity = activity as MainActivity
        mainViewModel = mainActivity.getViewModel()!!
        if (mainViewModel.tracksList.value == null || mainViewModel.tracksList.value?.isHandled() == false)
            mainViewModel.getMedia(TRACKS_ROOT)
        binding = FragmentTracksBinding.inflate(layoutInflater)
        mAdapter = TracksAdapter(requireContext(), glide, { clickedItem ->
            mainViewModel.itemClicked(clickedItem, requireContext())
        }, { item, pos ->
            val action = TracksFragmentDirections.actionTracksFragmentToSelectionFragment(
                null, null,
                Action.EDIT,
                Content.TRACK,
                TRACKS_ROOT, item.mediaId, (binding.recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            )
            findNavController().navigate(action)
        })
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            setItemViewCacheSize(16)
//            setHasFixedSize(true)
        }

        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = false
            mainViewModel.getMedia()
        }
        mainViewModel.tracksList.observe(viewLifecycleOwner, {
            mAdapter.setList(it.peekContent())
            Log.d("debug", "check")
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_icon -> {
                binding.recyclerView.scrollToPosition(15)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}