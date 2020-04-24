package com.snad.spotlight.ui.movieDetails

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.snad.spotlight.R
import com.snad.spotlight.databinding.RecyclerviewItemMovieDetailsCastBinding
import com.snad.spotlight.databinding.RecyclerviewItemMovieDetailsImagesBinding
import com.snad.spotlight.network.models.CastMember
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class CastAdapter(
    private val items: MutableList<CastMember>,
    var clickListener: (Int) -> Unit,
    var nameBackgroundColor: Int = Color.WHITE,
    var nameTextColor: Int = Color.DKGRAY
): RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val viewBinding = RecyclerviewItemMovieDetailsCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CastViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val item = items[position]

        holder.castCardView.setOnClickListener {
            clickListener(item.id)
        }

        val picasso = Picasso.get()
//        picasso.setIndicatorsEnabled(true)
        picasso.load("https://image.tmdb.org/t/p/w185${item.profile_path}")
            .resize(185, 278)
            .centerCrop()
            .placeholder(R.drawable.cover_image_placeholder)
            .error(R.drawable.cover_image_error)
            .transform(RoundedCornersTransformation(4, 1))
            .into(holder.castImageView)
        holder.nameTextView.text = "${item.name}\nas ${item.character}"
        holder.nameTextView.setTextColor(nameTextColor)
        holder.nameBackgroundView.backgroundTintList = ColorStateList.valueOf(nameBackgroundColor)
    }

    override fun getItemCount(): Int = items.size

    class CastViewHolder(
        viewBinding: RecyclerviewItemMovieDetailsCastBinding
    ): RecyclerView.ViewHolder(viewBinding.root) {
        val castCardView = viewBinding.castCardView
        val castImageView = viewBinding.castImageView
        val nameTextView = viewBinding.nameTextView
        val nameBackgroundView = viewBinding.nameBackgroundView
    }
}