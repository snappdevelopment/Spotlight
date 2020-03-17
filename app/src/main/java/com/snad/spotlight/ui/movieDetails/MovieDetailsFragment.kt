package com.snad.spotlight.ui.movieDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.snad.spotlight.App
import com.snad.spotlight.MovieDetailsRepository
import com.snad.spotlight.R
import com.snad.spotlight.databinding.FragmentMovieDetailsBinding
import com.snad.spotlight.persistence.LibraryDb
import com.snad.spotlight.persistence.models.LibraryMovie
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class MovieDetailsFragment: Fragment() {

    private lateinit var movieDetailsViewModel: MovieDetailsViewModel
    private var binding: FragmentMovieDetailsBinding? = null
    private val viewBinding: FragmentMovieDetailsBinding
        get() = binding!!

    private lateinit var addMovieFAB: FloatingActionButton
    private lateinit var loadingProgressBar: ContentLoadingProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val movieId = arguments?.getInt("id")
        movieId ?: throw NullPointerException("MovieId is null")

        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        addMovieFAB = viewBinding.addMovieFAB
        loadingProgressBar = viewBinding.loadingProgressbar

        val app = context!!.applicationContext as App
        val libraryDb = LibraryDb(app.appDb)
        val movieDetailsRepository = MovieDetailsRepository(libraryDb)

        movieDetailsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MovieDetailsViewModel(movieDetailsRepository) as T
            }
        }).get(MovieDetailsViewModel::class.java)

        movieDetailsViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when(state) {
                is MovieDetailsState.DoneState -> showDoneState(state.movie, state.isInLibrary)
                is MovieDetailsState.LoadingState -> showLoadingState()
                is MovieDetailsState.ErrorState -> showErrorState()
            }
        })

        addMovieFAB.setOnClickListener {
            movieDetailsViewModel.addMovieToLibrary()
        }

        movieDetailsViewModel.loadMovie(movieId)

        return viewBinding.root
    }

    private fun showDoneState(movie: LibraryMovie, isInLibrary: Boolean) {
        loadingProgressBar.hide()
        viewBinding.titleTextView.text = movie.title
        viewBinding.releaseDateTextView.text = movie.release_date.substring(0, 4)
        viewBinding.averageVoteTextView.text = movie.vote_average.toString()
        viewBinding.genreTextView.text = movie.genres
        when(movie.runtime) {
            null -> viewBinding.runtimeTextView.visibility = View.GONE
            else -> viewBinding.runtimeTextView.text = getString(R.string.movie_detail_runtime, movie.runtime)
        }
        when(movie.tagline) {
            null -> viewBinding.taglineTextView.visibility = View.GONE
            else -> viewBinding.taglineTextView.text = movie.tagline
        }
        when(movie.overview) {
            null -> viewBinding.overviewTextView.visibility = View.GONE
            else -> viewBinding.overviewTextView.text = movie.overview
        }
        when(isInLibrary) {
            true -> {
                viewBinding.addMovieFAB.isSelected = true
                //hasBeenWatched einblenden
                //hasBeenWatched.isSelected = movie.hasBeenWatched
            }
            false -> {
                viewBinding.addMovieFAB.isSelected = false
                //hasBeenWatched ausblenden
            }
        }
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w92${movie.poster_path}")
            .resize(92, 138)
            .centerCrop()
            .transform(RoundedCornersTransformation(4, 1))
            .into(viewBinding.coverImageView)
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w1280${movie.backdrop_path}")
            .fit()
            .transform(RoundedCornersTransformation(4, 1))
            .into(viewBinding.backdropImageView)
    }

    private fun showLoadingState() {
        loadingProgressBar.show()
    }

    private fun showErrorState() {
        loadingProgressBar.hide()
    }
}