package com.example.mplayer.Utility

import androidx.recyclerview.widget.DiffUtil

class ListDiffUtil<T>(
    private val oldList: MutableList<T>,
    private val newList: MutableList<T>
):DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int =newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        newList[newItemPosition]===oldList[oldItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        newList[newItemPosition]==oldList[oldItemPosition]
}