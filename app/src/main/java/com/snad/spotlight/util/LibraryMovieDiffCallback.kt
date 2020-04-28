package com.snad.spotlight.util

import androidx.recyclerview.widget.DiffUtil
import com.snad.spotlight.persistence.models.LibraryMovie

class LibraryMovieDiffCallback(
    private val oldList: List<LibraryMovie>,
    private val newList: List<LibraryMovie>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.has_been_watched == newItem.has_been_watched &&
                oldItem.title == newItem.title &&
                oldItem.release_date == newItem.release_date &&
                oldItem.vote_average == newItem.vote_average &&
                oldItem.runtime == newItem.runtime &&
                oldItem.genres == newItem.genres &&
                oldItem.backdrop_path == newItem.backdrop_path
    }
}