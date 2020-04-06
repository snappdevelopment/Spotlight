package com.snad.spotlight.ui.movieDetails

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.snad.spotlight.R
import com.snad.spotlight.databinding.RecyclerviewItemMovieDetailsCastBinding
import com.snad.spotlight.databinding.RecyclerviewItemMovieDetailsReviewsBinding
import com.snad.spotlight.network.models.Review
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class ReviewsAdapter(
    private val items: MutableList<Review>,
    var cardColor: Int = Color.WHITE,
    var authorTextColor: Int = Color.BLACK,
    var reviewTextColor: Int = Color.DKGRAY
): RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val viewBinding = RecyclerviewItemMovieDetailsReviewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val item = items[position]

        holder.authorTextView.text = item.author
        holder.authorTextView.setTextColor(authorTextColor)
        holder.reviewTextView.text = item.content
        holder.reviewTextView.setTextColor(reviewTextColor)
        holder.reviewCardView.setCardBackgroundColor(cardColor)
    }

    override fun getItemCount(): Int = items.size

    class ReviewViewHolder(
        viewBinding: RecyclerviewItemMovieDetailsReviewsBinding
    ): RecyclerView.ViewHolder(viewBinding.root) {
        val reviewCardView = viewBinding.reviewCardView
        val authorTextView = viewBinding.authorTextView
        val reviewTextView = viewBinding.reviewTextView
    }
}