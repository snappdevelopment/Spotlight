package com.snad.feature.moviedetails

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.snad.core.persistence.models.CastMember
import com.snad.feature.moviedetails.databinding.RecyclerviewItemMovieDetailsCastBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

internal class CastAdapter(
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
        holder.nameTextView.text = item.name
        holder.nameTextView.setTextColor(nameTextColor)
        if (item.character == "") {
            holder.characterTextView.text = holder.itemView.context.getString(
                R.string.movie_detail_cast_character,
                holder.itemView.context.getString(R.string.movie_detail_cast_character_unknown)
            )
        }
        else {
            holder.characterTextView.text = holder.itemView.context.getString(
                R.string.movie_detail_cast_character,
                item.character
            )
        }
        holder.characterTextView.setTextColor(nameTextColor)
        holder.nameBackgroundView.backgroundTintList = ColorStateList.valueOf(nameBackgroundColor)
    }

    override fun getItemCount(): Int = items.size

    inner class CastViewHolder(
        viewBinding: RecyclerviewItemMovieDetailsCastBinding
    ): RecyclerView.ViewHolder(viewBinding.root) {
        val castCardView = viewBinding.castCardView
        val castImageView = viewBinding.castImageView
        val nameTextView = viewBinding.nameTextView
        val characterTextView = viewBinding.characterTextView
        val nameBackgroundView = viewBinding.nameBackgroundView
    }
}