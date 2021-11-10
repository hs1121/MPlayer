package com.example.mplayer.Utility

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mplayer.Adapters.PlaylistAdapter

class DragAndDropHelper(
    val adapter: PlaylistAdapter
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,0) {


    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
       val dragPos = viewHolder.adapterPosition
        val targetPos = target.adapterPosition

        adapter.itemMoved(dragPos,targetPos)


        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("Not yet implemented")
    }


}