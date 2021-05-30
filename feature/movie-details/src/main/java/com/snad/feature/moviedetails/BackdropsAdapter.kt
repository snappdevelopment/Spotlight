package com.snad.feature.moviedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.snad.core.persistence.models.Image
import com.snad.feature.moviedetails.databinding.RecyclerviewItemMovieDetailsImagesBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

internal class BackdropsAdapter(
    private val items: MutableList<Image>
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

    inner class ImagesViewHolder(
        viewBinding: RecyclerviewItemMovieDetailsImagesBinding
    ): RecyclerView.ViewHolder(viewBinding.root) {
        val imageImageView = viewBinding.imageImageView
    }
}