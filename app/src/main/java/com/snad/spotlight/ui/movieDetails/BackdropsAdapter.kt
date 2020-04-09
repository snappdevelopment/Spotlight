package com.snad.spotlight.ui.movieDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.snad.spotlight.databinding.RecyclerviewItemMovieDetailsImagesBinding
import com.snad.spotlight.network.models.Backdrop
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class BackdropsAdapter(
    private val items: MutableList<Backdrop>
): RecyclerView.Adapter<BackdropsAdapter.ImagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val viewBinding = RecyclerviewItemMovieDetailsImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImagesViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val item = items[position]
        val picasso = Picasso.get()
//        picasso.setIndicatorsEnabled(true)
        picasso.load("https://image.tmdb.org/t/p/w780${item.file_path}")
            .resize(780, 439)
            .centerCrop()
            .transform(RoundedCornersTransformation(4, 1))
            .into(holder.imageImageView)
    }

    override fun getItemCount(): Int = items.size

    class ImagesViewHolder(
        viewBinding: RecyclerviewItemMovieDetailsImagesBinding
    ): RecyclerView.ViewHolder(viewBinding.root) {
        val imageImageView = viewBinding.imageImageView
    }
}