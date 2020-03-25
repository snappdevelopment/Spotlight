package com.snad.spotlight.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.snad.spotlight.R
import com.snad.spotlight.databinding.RecyclerviewItemNewMoviesBinding
import com.snad.spotlight.databinding.RecyclerviewItemSearchBinding
import com.snad.spotlight.network.models.ListMovie
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class SearchAdapter(
    private val items: MutableList<ListMovie>,
    private val clickListener: (Int) -> Unit
): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val viewBinding = RecyclerviewItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = items[position]
        holder.movieCard.setOnClickListener {
            clickListener(item.id)
        }
        val picasso = Picasso.get()
//        picasso.setIndicatorsEnabled(true)
        picasso.load("https://image.tmdb.org/t/p/w92${item.poster_path}")
            .resize(92, 138)
            .centerCrop()
            .placeholder(R.drawable.cover_image_placeholder)
            .error(R.drawable.cover_image_error)
            .transform(RoundedCornersTransformation(4, 1))
            .into(holder.coverImageView)
        //Todo: placeholder und error image mit app icon
        holder.titleTextView.text = item.title
        if(item.release_date == "") holder.releaseDateTextView.visibility = View.GONE
        else holder.releaseDateTextView.text = item.release_date.substring(0, 4)
        holder.overviewTextView.text = item.overview
        holder.averageVoteTextView.text = item.vote_average.toString()
    }

    override fun getItemCount(): Int = items.size

    class SearchViewHolder(
        viewBinding: RecyclerviewItemSearchBinding
    ): RecyclerView.ViewHolder(viewBinding.root) {
        val movieCard = viewBinding.movieCard
        val coverImageView = viewBinding.coverImageView
        val titleTextView = viewBinding.titleTextView
        val releaseDateTextView = viewBinding.releaseDateTextView
        val overviewTextView = viewBinding.overviewTextView
        val averageVoteTextView = viewBinding.averageVoteTextView
    }
}