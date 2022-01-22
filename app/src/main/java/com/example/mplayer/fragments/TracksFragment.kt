package com.example.mplayer.fragments

import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.mplayer.Adapters.TracksAdapter
import com.example.mplayer.Constants.TRACKS_ROOT
import com.example.mplayer.MainActivity
import com.example.mplayer.R
import com.example.mplayer.Utility.Action
import com.example.mplayer.Utility.Content
import com.example.mplayer.Utility.PreferenceDataStore
import com.example.mplayer.viewModels.MainViewModel
import com.example.mplayer.databinding.FragmentTracksBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

const val SEARCH_INTERVAL = 500L

@AndroidEntryPoint
class TracksFragment : Fragment() {


    @Inject
    lateinit var glide: RequestManager
    @Inject
    lateinit var dataStore: PreferenceDataStore
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentTracksBinding
    private lateinit var mAdapter: TracksAdapter

    private var searchEnabled = true
    set(value) {
        field=value
        performSearch?.let { it() }
    }
    private  var performSearch : (() -> Unit?)? =null

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
                null,
                null,
                Action.EDIT,
                Content.TRACK,
                TRACKS_ROOT,
                item.mediaId,
                (binding.recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
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
            mainViewModel.refreshMedia()
        }
        mainViewModel.tracksList.observe(viewLifecycleOwner, {
            if (it.peekContent().isEmpty()){
                binding.refreshLayout.visibility=View.GONE
                binding.noSongText.visibility=View.VISIBLE
            }
            else {
                binding.refreshLayout.visibility = View.VISIBLE
                binding.noSongText.visibility = View.GONE
                mAdapter.setList(it.peekContent())
                mAdapter.notifyDataSetChanged()

            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val search = menu.findItem(R.id.search_icon)
        val sort = menu.findItem(R.id.sort_icon)
        sort.isVisible = true
        search.isVisible = true
        val searchView = search?.actionView as SearchView
        searchView.apply {
            isSubmitButtonEnabled = true
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true;
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                     if (searchEnabled) {
                      performSearch(newText,searchView)
                    }
                    return true

                }

            })

//            setOnQueryTextFocusChangeListener { _, hasFocus ->
//                binding.refreshLayout.isRefreshing = !hasFocus
//               }
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_icon -> {
               val sortView:View?= requireActivity().findViewById(item.itemId)
               val popupMenu= sortView?.let {
                   PopupMenu(requireContext(), it).apply {
                       inflate(R.menu.sort_menu)
                       setOnMenuItemClickListener(this)
                   }
               }
                   popupMenuInit(popupMenu)
                    popupMenu?.show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun popupMenuInit(itemMenu: PopupMenu?) {
        itemMenu?.let {
            val ascCheckBox= it.menu.findItem(R.id.is_asc)

            lifecycleScope.launch(Dispatchers.Main) {
                val data= dataStore.readSotData()
                ascCheckBox.isChecked=data.isChecked;
             //   val id=R.id.sort_name
                val sortBy:MenuItem?=it.menu.findItem(data.id)
                sortBy?.isChecked=true
            }


    }



    }

    private fun setOnMenuItemClickListener(popupMenu: PopupMenu) {
        popupMenu.setOnMenuItemClickListener {menuItem->
             when(menuItem.itemId){
                 R.id.is_asc->{
                         menuItem.isChecked= !menuItem.isChecked
                     mainViewModel.updateSort(menuItem.isChecked,null)
                     lifecycleScope.launch {
                         dataStore.saveSortData(menuItem.isChecked, null)
                     }
                 }
                 R.id.sort_name->{
                     menuItem.isChecked= true
                     mainViewModel.updateSort(null,menuItem.itemId)
                     lifecycleScope.launch {
                         dataStore.saveSortData(null,menuItem.itemId)
                     }
                 }
                 R.id.sort_date->{
                     menuItem.isChecked= true
                     mainViewModel.updateSort(null,menuItem.itemId)
                     lifecycleScope.launch {
                         dataStore.saveSortData(null,menuItem.itemId)
                     }
                 }

             }

            val isAsc=popupMenu.menu.findItem(R.id.is_asc)


            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
            menuItem.actionView = View(context)
            menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return false
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    return false
                }
            })

            false
        }

    }

    private fun performSearch(newText: String?, searchView: SearchView){
        searchEnabled = false
        lifecycleScope.launch {
            val oldList = mainViewModel.tracksList.value?.peekContent()
            val newList = mutableListOf<MediaBrowserCompat.MediaItem>()
            oldList?.forEach { item ->
                val text=item.description.title.toString().lowercase()
                if(newText?.lowercase()?.let { text.contains(it) } == true){
                    newList.add(item)
                }
            }
            mAdapter.setList(newList)
            delay(SEARCH_INTERVAL)
            if(newText!=searchView.query){
                performSearch(searchView.query.toString(),searchView) // to check that the last search was performed
            }
            searchEnabled=true

        }
    }

}