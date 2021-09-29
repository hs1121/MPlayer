package com.example.mplayer.Adapters

import android.content.Context
import android.media.browse.MediaBrowser
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest.Builder.fromUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.mplayer.R
import com.example.mplayer.Utility.ListDiffUtil
import com.example.mplayer.databinding.FragmentTracksBinding
import com.example.mplayer.databinding.MusicItemListLayoutBinding
import com.example.mplayer.exoPlayer.SongEntity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject


class TracksAdapter(private val context: Context,private val glide: RequestManager): RecyclerView.Adapter<TracksAdapter.TracksViewHolder>() {


    private var list= mutableListOf<MediaBrowserCompat.MediaItem>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val binding: MusicItemListLayoutBinding = MusicItemListLayoutBinding.inflate(LayoutInflater.from(context))
        return TracksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val item = list[position]
        val binding = holder.binding
        if (item.isPlayable) {
            binding.itemTitle.text = item.description.title
            binding.itemSubtitle.text = item.description.subtitle
//
//            glide.load(item.description.iconUri)
//                .apply(RequestOptions().override(100, 100))
//                .into(binding.itemImage)

            binding.root.setOnClickListener {
                val exoPlayer = SimpleExoPlayer.Builder(context).build().apply {

                    setHandleAudioBecomingNoisy(true)
                }
                val mediaItem = item.description.mediaUri?.let { it1 -> MediaItem.fromUri(it1) }
                if (mediaItem != null) {
                    exoPlayer.setMediaItem(mediaItem)
                }
                exoPlayer.prepare()
                exoPlayer.play()
            }

        }else{
            binding.itemTitle.text = "browsable"
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

    inner class TracksViewHolder( val binding:MusicItemListLayoutBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.itemMove.visibility= View.GONE
        }
    }
}