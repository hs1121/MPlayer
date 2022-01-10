package com.example.mplayer.Adapters

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.mplayer.R
import com.example.mplayer.Utility.AddPlaylistDialog
import com.example.mplayer.Utility.ListDiffUtil
import com.example.mplayer.databinding.MusicItemListLayoutBinding

class PlaylistAdapter (
    private val context: Context,
    private val glide: RequestManager): RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {


    private var list = mutableListOf<MediaBrowserCompat.MediaItem>()
    private lateinit var playlistListener:PlaylistItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding: MusicItemListLayoutBinding =
            MusicItemListLayoutBinding.inflate(LayoutInflater.from(context))
        return PlaylistViewHolder(binding)
    }


    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val item = list[position]
        val binding = holder.binding
//        if (list.lastIndex==position)
//            binding.endLine.visibility=View.INVISIBLE
        if (item.isPlayable) {

            binding.itemMove.visibility=View.VISIBLE
            binding.itemTitle.text = item.description.title
            binding.itemSubtitle.text = item.description.subtitle

            glide.load(item.description.iconUri)
                .apply(RequestOptions().override(100, 100))
                .into(binding.itemImage)


        } else {
            binding.itemTitle.text = item.description.title
            glide.load(item.description.iconUri)
                .apply(RequestOptions().override(100, 100))
                .into(binding.itemImage)
            binding.itemSubtitle.visibility = View.GONE
            binding.itemMore.visibility = View.VISIBLE
            binding.itemMove.visibility = View.GONE
        }

        binding.itemMore.setOnClickListener {
            val popupMenu=PopupMenu(context,binding.itemMore)
            popupMenu.inflate(R.menu.list_menu)


            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.edit_item->{
                        val dialog=AddPlaylistDialog(item.mediaId,item.description.subtitle.toString()){name,by->
                            playlistListener.onItemEdited(item.mediaId!!,name,by,item)
                        }
                        playlistListener.getFragmentManagerInstance()?.let { manager-> dialog.show(manager,"edit")  }
                        true
                    }
                    R.id.delete_item->{
                        playlistListener.onItemDelete(item)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        binding.root.setOnClickListener {
            playlistListener.onItemClick(item)
        }
        binding.root.setOnLongClickListener {
            playlistListener.onLongClick(item,position)
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun setList(list: MutableList<MediaBrowserCompat.MediaItem>) {
        val diffUtil = ListDiffUtil(this.list, list)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        this.list = list
        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun itemMoved(dragPos: Int, targetPos: Int) {
        // changes list data as well as update recyclerview on item drag(playlist items arrange functionality)
        notifyItemMoved(dragPos,targetPos)
        val item=list[dragPos]
        list[dragPos]=list[targetPos]
        list[targetPos]=item
    }
    fun getList()=list

    inner class PlaylistViewHolder(val binding: MusicItemListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface PlaylistItemListener{
        fun onItemClick(item:MediaBrowserCompat.MediaItem)
        fun onLongClick(item:MediaBrowserCompat.MediaItem,position: Int)
        fun onItemEdited(id:String,name: String, by: String, item: MediaBrowserCompat.MediaItem)
        fun onItemDelete(item:MediaBrowserCompat.MediaItem)
        fun getFragmentManagerInstance(): FragmentManager? =null
    }
    fun setPlaylistItemListener(playlistListener:PlaylistItemListener){
        this.playlistListener=playlistListener
    }

}