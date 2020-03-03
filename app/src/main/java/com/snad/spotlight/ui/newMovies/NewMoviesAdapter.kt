package com.snad.spotlight.ui.newMovies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.snad.spotlight.databinding.RecyclerviewItemNewMoviesBinding

class NewMoviesAdapter(private val items: MutableList<String>): RecyclerView.Adapter<NewMoviesAdapter.NewMoviesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMoviesViewHolder {
        val viewBinding = RecyclerviewItemNewMoviesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewMoviesViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: NewMoviesViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item
    }

    override fun getItemCount(): Int = items.size

    class NewMoviesViewHolder(
        viewBinding: RecyclerviewItemNewMoviesBinding
    ): RecyclerView.ViewHolder(viewBinding.root) {
        val textView = viewBinding.textView
    }
}