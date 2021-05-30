package com.snad.feature.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.snad.feature.library.databinding.RecyclerviewItemLibraryBinding
import com.snad.core.persistence.models.LibraryMovie
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import javax.inject.Inject

internal class LibraryAdapter @Inject constructor()
    : ListAdapter<LibraryMovie, LibraryAdapter.LibraryViewHolder>(LibraryMovieDiffCallback) {

    var longClickListener: ((LibraryMovie) -> Unit)? = null
    var clickListener: ((Int, CardView) -> Unit)? = null
    var watchedClickListener: ((LibraryMovie) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val binding = RecyclerviewItemLibraryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibraryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int, payloads: MutableList<Any>) {
        val item = getItem(position)
        val payload = payloads.firstOrNull() as? Boolean

        holder.binding.movieCard.setOnLongClickListener {
            longClickListener?.invoke(item)
            true
        }
        holder.binding.movieCard.setOnClickListener {
            clickListener?.invoke(item.id, holder.binding.coverCardView)
        }
        holder.binding.hasBeenWatchedFAB.setOnClickListener {
            val watchedItem = item.copy(has_been_watched = !item.has_been_watched)
            watchedClickListener?.invoke(watchedItem)
        }

        if(payload != null) holder.update(item)
        else holder.bind(item)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        onBindViewHolder(holder, position, mutableListOf())
    }

    inner class LibraryViewHolder(
        val binding: RecyclerviewItemLibraryBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun update(item: LibraryMovie) {
            binding.hasBeenWatchedFAB.isSelected = item.has_been_watched
        }

        fun bind(item: LibraryMovie) {
            binding.coverCardView.transitionName = "cover${item.id}"
            binding.hasBeenWatchedFAB.isSelected = item.has_been_watched
            val picasso = Picasso.get()
//        picasso.setIndicatorsEnabled(true)
            picasso.load("https://image.tmdb.org/t/p/w154${item.poster_path}")
                .resize(154, 231)
                .centerCrop()
                .placeholder(R.drawable.cover_image_placeholder)
                .error(R.drawable.cover_image_error)
                .transform(RoundedCornersTransformation(4, 1))
                .into(binding.coverImageView)

            binding.titleTextView.text = item.title
            if(item.release_date == "") binding.releaseDateTextView.visibility = View.GONE
            else binding.releaseDateTextView.text = item.release_date.substring(0, 4)
            binding.runtimeTextView.text = if(item.runtime != null) "${item.runtime} min" else ""
            binding.averageVoteTextView.text = item.vote_average.toString()
            binding.genreTextView.text = item.genres
        }
    }

    object LibraryMovieDiffCallback: DiffUtil.ItemCallback<LibraryMovie>() {
        override fun areItemsTheSame(oldItem: LibraryMovie, newItem: LibraryMovie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LibraryMovie, newItem: LibraryMovie): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: LibraryMovie, newItem: LibraryMovie): Any? {
            return newItem.has_been_watched
        }
    }
}