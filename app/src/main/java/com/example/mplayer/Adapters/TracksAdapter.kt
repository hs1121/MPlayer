package com.example.mplayer.Adapters

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.mplayer.Utility.ListDiffUtil
import com.example.mplayer.databinding.MusicItemListLayoutBinding


class TracksAdapter(
    private val context: Context,
    private val glide: RequestManager,
     val itemClickListener:(MediaBrowserCompat.MediaItem)->Unit ): RecyclerView.Adapter<TracksAdapter.TracksViewHolder>() {


    private var list= mutableListOf<MediaBrowserCompat.MediaItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val binding: MusicItemListLayoutBinding = MusicItemListLayoutBinding.inflate(LayoutInflater.from(context))
        return TracksViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val item = list[position]
        val binding = holder.binding
//        if (list.lastIndex==position)
//            binding.endLine.visibility=View.INVISIBLE
        if (item.isPlayable) {
            binding.itemTitle.text = item.description.title
            binding.itemSubtitle.text = item.description.subtitle

            glide.load(item.description.iconUri)
                .apply(RequestOptions().override(100, 100))
                .into(binding.itemImage)



        }else{
          binding.itemTitle.text=item.description.title
            glide.load(item.description.iconUri)
                .apply(RequestOptions().override(100, 100))
                .into(binding.itemImage)
            binding.itemSubtitle.visibility=View.GONE
            binding.itemMore.visibility=View.GONE
            binding.itemMove.visibility=View.GONE
        }
        binding.root.setOnClickListener {
            itemClickListener(item)
        }
    }

    override fun getItemCount(): Int {
     return list.size
    }


    fun setList(list:MutableList<MediaBrowserCompat.MediaItem>){

        val diffUtil=ListDiffUtil(this.list,list)
        val diffUtilResult=DiffUtil.calculateDiff(diffUtil)
        this.list=list
        diffUtilResult.dispatchUpdatesTo(this)
    }

    inner class TracksViewHolder( val binding:MusicItemListLayoutBinding): RecyclerView.ViewHolder(binding.root)
}