package com.snad.spotlight.ui.castDetails

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.snad.spotlight.R
import com.snad.spotlight.databinding.RecyclerviewItemCastDetailsKnownForBinding
import com.snad.spotlight.network.models.Cast
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class KnownForAdapter(
    private val items: MutableList<Cast>,
    var cardBackgroundColor: Int = Color.WHITE,
    var textColor: Int = Color.DKGRAY
): RecyclerView.Adapter<KnownForAdapter.KnownForViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KnownForViewHolder {
        val viewBinding = RecyclerviewItemCastDetailsKnownForBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KnownForViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: KnownForViewHolder, position: Int) {
        val item = items[position]

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
        holder.titleTextView.setTextColor(textColor)
        if(item.release_date == null ||item.release_date == "") holder.releaseDateTextView.visibility = View.GONE
        else {
            holder.releaseDateTextView.text = item.release_date.substring(0, 4)
            holder.releaseDateTextView.setTextColor(textColor)
        }
        holder.averageVoteTextView.text = item.vote_average.toString()
        holder.averageVoteTextView.setTextColor(textColor)
        if(item.character == "") holder.characterTextView.visibility = View.GONE
        else {
            holder.characterTextView.text = holder.itemView.context.getString(R.string.cast_detail_known_for_character, item.character)
            holder.characterTextView.setTextColor(textColor)
        }
        holder.movieCardView.backgroundTintList = ColorStateList.valueOf(cardBackgroundColor)
    }

    override fun getItemCount(): Int = items.size

    class KnownForViewHolder(
        viewBinding: RecyclerviewItemCastDetailsKnownForBinding
    ): RecyclerView.ViewHolder(viewBinding.root) {
        val movieCardView = viewBinding.movieCard
        val coverImageView = viewBinding.coverImageView
        val titleTextView = viewBinding.titleTextView
        val releaseDateTextView = viewBinding.releaseDateTextView
        val averageVoteTextView = viewBinding.averageVoteTextView
        val characterTextView = viewBinding.characterTextView
    }
}