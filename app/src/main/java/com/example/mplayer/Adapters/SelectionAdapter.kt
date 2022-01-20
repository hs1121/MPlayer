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

class SelectionAdapter (
    private val context: Context,
    private val glide: RequestManager,
    private val selectedItem:String?,
    private val pos: Int?): RecyclerView.Adapter<SelectionAdapter.SelectionViewHolder>() {


    private var list= mutableListOf<MediaBrowserCompat.MediaItem>()
    private var selectedItems= mutableListOf<MediaBrowserCompat.MediaItem>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionViewHolder {
        val binding: MusicItemListLayoutBinding = MusicItemListLayoutBinding.inflate(LayoutInflater.from(context))
        return SelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectionViewHolder, position: Int) {
        val item = list[position]
        val binding = holder.binding
        if (selectedItem!=null&&item.mediaId==selectedItem&&position==pos){
            binding.checkBox.isChecked=true
            selectedItems.add(item)
        }

//        if (list.lastIndex==position)
//            binding.endLine.visibility= View.INVISIBLE
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
            binding.itemSubtitle.visibility= View.GONE
        }
        binding.checkBox.setOnClickListener {
            if (binding.checkBox.isChecked)
                selectedItems.add(item)
            else
                selectedItems.remove(item)
        }
        binding.root.setOnClickListener {
            if (!binding.checkBox.isChecked) {
                selectedItems.add(item)
                binding.checkBox.isChecked=true
            }
            else {
                selectedItems.remove(item)
                binding.checkBox.isChecked=false
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    // Returns URI of selected media items(for deleting purpose etc.);
    fun getUris()=selectedItems.map { it.description.mediaUri!! }

    fun getSelectedItems()=selectedItems

    // returns unSelected media items (to update list with these items after performing operation like delete)
    fun getUnSelectedItems()=run {
        val newList=list
        selectedItems.forEach {  item->
            newList.remove(item)
        }
        return@run newList
    }


    fun setList(list:MutableList<MediaBrowserCompat.MediaItem>){

        val diffUtil= ListDiffUtil(this.list,list)
        val diffUtilResult= DiffUtil.calculateDiff(diffUtil)
        this.list=list
        diffUtilResult.dispatchUpdatesTo(this)



    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView=recyclerView
    }

    inner class SelectionViewHolder(val binding: MusicItemListLayoutBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.checkBox.visibility=View.VISIBLE
            binding.itemMove.visibility=View.GONE
            binding.itemMore.visibility=View.GONE
        }
    }
}