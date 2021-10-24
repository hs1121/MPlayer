package com.example.mplayer.Adapters

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.mplayer.Utility.ListDiffUtil
import com.example.mplayer.databinding.MusicItemListLayoutBinding

class AlbumAdapter (
    private val context: Context,
    val itemClickListener:(String)->Unit ): RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {


    private var list = mutableListOf<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding: MusicItemListLayoutBinding = MusicItemListLayoutBinding.inflate(
            LayoutInflater.from(context)
        )
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val item = list[position]
        val binding = holder.binding
        if (list.lastIndex == position)
            binding.endLine.visibility = View.INVISIBLE
        binding.itemTitle.text=item

        binding.root.setOnClickListener {
            itemClickListener(item)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(list: MutableList<String>) {

        val diffUtil = ListDiffUtil(this.list, list)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        this.list = list
        diffUtilResult.dispatchUpdatesTo(this)
    }

    inner class AlbumViewHolder(val binding: MusicItemListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root){
            init {
                binding.itemMore.visibility=GONE
                binding.itemSubtitle.visibility=GONE
            }
        }

}