package com.snad.feature.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.snad.feature.search.model.ListMovie
import com.snad.feature.search.databinding.RecyclerviewItemSearchBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

internal class SearchAdapter(
    private val items: MutableList<ListMovie>,
    private val clickListener: (Int, CardView) -> Unit
): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val viewBinding = RecyclerviewItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = items[position]

        holder.coverCardView.transitionName = "cover${item.id}"

        holder.movieCard.setOnClickListener {
            clickListener(item.id, holder.coverCardView)
        }
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
        if(item.release_date == null || item.release_date == "") holder.releaseDateTextView.visibility = View.GONE
        else holder.releaseDateTextView.text = item.release_date.substring(0, 4)
        holder.overviewTextView.text = item.overview
        holder.averageVoteTextView.text = item.vote_average.toString()
    }

    override fun getItemCount(): Int = items.size

    inner class SearchViewHolder(
        viewBinding: RecyclerviewItemSearchBinding
    ): RecyclerView.ViewHolder(viewBinding.root) {
        val movieCard = viewBinding.movieCard
        val coverImageView = viewBinding.coverImageView
        val coverCardView = viewBinding.coverCardView
        val titleTextView = viewBinding.titleTextView
        val releaseDateTextView = viewBinding.releaseDateTextView
        val overviewTextView = viewBinding.overviewTextView
        val averageVoteTextView = viewBinding.averageVoteTextView
    }
}