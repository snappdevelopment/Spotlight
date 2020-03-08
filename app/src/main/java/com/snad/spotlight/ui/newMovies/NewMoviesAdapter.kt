package com.snad.spotlight.ui.newMovies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.snad.spotlight.databinding.RecyclerviewItemNewMoviesBinding
import com.snad.spotlight.network.models.NewMovie
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class NewMoviesAdapter(private val items: MutableList<NewMovie>): RecyclerView.Adapter<NewMoviesAdapter.NewMoviesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMoviesViewHolder {
        val viewBinding = RecyclerviewItemNewMoviesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewMoviesViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: NewMoviesViewHolder, position: Int) {
        val item = items[position]
        val picasso = Picasso.get()
        picasso.setIndicatorsEnabled(true)
        picasso.load("https://image.tmdb.org/t/p/w92${item.poster_path}")
            .resize(92, 138)
            .centerCrop()
            .transform(RoundedCornersTransformation(2, 1))
            .into(holder.coverImageView)
        //Todo: placeholder und error image mit app icon
        holder.titleTextView.text = item.title
        holder.releaseDateTextView.text = item.release_date.substring(0, 4)
        holder.overviewTextView.text = item.overview
        holder.averageVoteTextView.text = item.vote_average.toString()
    }

    override fun getItemCount(): Int = items.size

    class NewMoviesViewHolder(
        viewBinding: RecyclerviewItemNewMoviesBinding
    ): RecyclerView.ViewHolder(viewBinding.root) {
        val coverImageView = viewBinding.coverImageView
        val titleTextView = viewBinding.titleTextView
        val releaseDateTextView = viewBinding.releaseDateTextView
        val overviewTextView = viewBinding.overviewTextView
        val averageVoteTextView = viewBinding.averageVoteTextView
    }
}