package com.snad.spotlight.ui.movieDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.snad.spotlight.databinding.FragmentMovieDetailsBinding

class MovieDetailsFragment: Fragment() {

    private lateinit var movieDetailsViewModel: MovieDetailsViewModel
    private var binding: FragmentMovieDetailsBinding? = null
    private val viewBinding: FragmentMovieDetailsBinding
        get() = binding!!

    private lateinit var titleTextView: TextView
    private lateinit var addMovieImageButton: ImageButton



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        titleTextView = viewBinding.titleTextView
        addMovieImageButton = viewBinding.addMovieImageButton

        movieDetailsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MovieDetailsViewModel() as T
            }
        }).get(MovieDetailsViewModel::class.java)



        return viewBinding.root
    }
}