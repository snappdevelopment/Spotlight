package com.snad.spotlight.ui.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.snad.spotlight.R
import com.snad.spotlight.databinding.RecyclerviewItemLibraryBinding
import com.snad.spotlight.persistence.models.LibraryMovie
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class LibraryAdapter(
    private val items: MutableList<LibraryMovie>,
    private val longClickListener: (LibraryMovie) -> Unit,
    private val clickListener: (Int, ImageView) -> Unit,
    private val watchedClickListener: (LibraryMovie) -> Unit
): RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val viewBinding = RecyclerviewItemLibraryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibraryViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val item = items[position]

        holder.coverImageView.transitionName = "cover${item.id}"

        holder.movieCard.setOnLongClickListener {
            longClickListener(item)
            true
        }
        holder.movieCard.setOnClickListener {
            clickListener(
                item.id,
                holder.coverImageView
            )
        }
        holder.hasBeenWatchedFAB.setOnClickListener {
            val watchedItem = item.copy(has_been_watched = !item.has_been_watched)
            watchedClickListener(watchedItem)
        }
        holder.hasBeenWatchedFAB.isSelected = item.has_been_watched
        val picasso = Picasso.get()
//        picasso.setIndicatorsEnabled(true)
        picasso.load("https://image.tmdb.org/t/p/w154${item.poster_path}")
            .resize(154, 231)
            .centerCrop()
            .placeholder(R.drawable.cover_image_placeholder)
            .error(R.drawable.cover_image_error)
            .transform(RoundedCornersTransformation(4, 1))
            .into(holder.coverImageView)

        holder.titleTextView.text = item.title
        if(item.release_date == "") holder.releaseDateTextView.visibility = View.GONE
        else holder.releaseDateTextView.text = item.release_date.substring(0, 4)
        holder.runtimeTextView.text = if(item.runtime != null) "${item.runtime} min" else ""
        holder.averageVoteTextView.text = item.vote_average.toString()
        holder.genreTextView.text = item.genres
    }

    override fun getItemCount(): Int = items.size

    class LibraryViewHolder(
        viewBinding: RecyclerviewItemLibraryBinding
    ): RecyclerView.ViewHolder(viewBinding.root) {
        val movieCard = viewBinding.movieCard
        val coverImageView = viewBinding.coverImageView
        val titleTextView = viewBinding.titleTextView
        val releaseDateTextView = viewBinding.releaseDateTextView
        val runtimeTextView = viewBinding.runtimeTextView
        val averageVoteTextView = viewBinding.averageVoteTextView
        val genreTextView = viewBinding.genreTextView
        val hasBeenWatchedFAB = viewBinding.hasBeenWatchedFAB
    }
}