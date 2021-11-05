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
import com.example.mplayer.database.entity.PlaylistEntity
import com.example.mplayer.databinding.MusicItemListLayoutBinding


class PlaylistAdapter(
    private val context: Context,
    private val glide: RequestManager,
    val itemClickListener:(PlaylistEntity)->Unit ): RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {


    private var list= mutableListOf<PlaylistEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding: MusicItemListLayoutBinding = MusicItemListLayoutBinding.inflate(LayoutInflater.from(context))
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val item = list[position]
        val binding = holder.binding
        if (list.lastIndex==position)
            binding.endLine.visibility=View.INVISIBLE

            binding.itemTitle.text=item.name
            binding.itemSubtitle.text=item.createdBy


            glide.load(item.iconUri)
                .apply(RequestOptions().override(100, 100))
                .into(binding.itemImage)

        binding.root.setOnClickListener {
            itemClickListener(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun setList(list:MutableList<PlaylistEntity>){

        val diffUtil=ListDiffUtil(this.list,list)
        val diffUtilResult=DiffUtil.calculateDiff(diffUtil)
        this.list=list
        diffUtilResult.dispatchUpdatesTo(this)
    }

    inner class PlaylistViewHolder( val binding:MusicItemListLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.checkBox.visibility = View.GONE
            binding.itemMove.visibility = View.GONE
            binding.itemMore.visibility = View.VISIBLE
        }
    }
}