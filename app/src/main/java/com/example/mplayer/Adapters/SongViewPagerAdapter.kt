package com.example.mplayer.Adapters

import android.support.v4.media.MediaBrowserCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.mplayer.Utility.ListDiffUtil
import com.example.mplayer.databinding.MusicItemListLayoutBinding
import com.example.mplayer.databinding.SwipesSongLayoutBinding

class SongViewPagerAdapter: RecyclerView.Adapter<SongViewPagerAdapter.ViewHolder>() {

    private var list= mutableListOf<MediaBrowserCompat.MediaItem>()
    class ViewHolder(val binding:SwipesSongLayoutBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:  SwipesSongLayoutBinding = SwipesSongLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.binding.tvPrimary.text=list[position].description.title
    }

    override fun getItemCount(): Int {
       return list.size
    }
    fun setList(list:MutableList<MediaBrowserCompat.MediaItem>){

        val diffUtil= ListDiffUtil(this.list,list)
        val diffUtilResult= DiffUtil.calculateDiff(diffUtil)
        this.list=list
        diffUtilResult.dispatchUpdatesTo(this)
    }
}