package com.snad.feature.moviedetails

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.snad.core.persistence.models.Review
import com.snad.feature.moviedetails.databinding.RecyclerviewItemMovieDetailsReviewsBinding

internal class ReviewsAdapter(
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

    inner class ReviewViewHolder(
        viewBinding: RecyclerviewItemMovieDetailsReviewsBinding
    ): RecyclerView.ViewHolder(viewBinding.root) {
        val reviewCardView = viewBinding.reviewCardView
        val authorTextView = viewBinding.authorTextView
        val reviewTextView = viewBinding.reviewTextView
    }
}