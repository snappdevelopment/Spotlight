package com.snad.spotlight.ui.movieDetails

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.snad.spotlight.network.*
import com.snad.spotlight.persistence.LibraryDb
import com.snad.spotlight.persistence.models.LibraryMovie
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MovieDetailsFragment: Fragment() {

    private lateinit var movieDetailsViewModel: MovieDetailsViewModel
    private var binding: FragmentMovieDetailsBinding? = null
    private val viewBinding: FragmentMovieDetailsBinding
        get() = binding!!

    private lateinit var addOrRemoveMovieFAB: FloatingActionButton
    private lateinit var loadingProgressBar: ContentLoadingProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val movieId = arguments?.getInt("id")
        movieId ?: throw NullPointerException("MovieId is null")

        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        addOrRemoveMovieFAB = viewBinding.addOrRemoveMovieFAB
        loadingProgressBar = viewBinding.loadingProgressbar

        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val cache = Cache(activity!!.cacheDir, cacheSize.toLong())

        val httpClient = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(ApiKeyInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val movieService = retrofit.create<MovieService>(MovieService::class.java)

        val app = context!!.applicationContext as App
        val libraryDb = LibraryDb(app.appDb)
        val movieApi = MovieApi(movieService)
        val movieDetailsRepository = MovieDetailsRepository(libraryDb, movieApi)

        movieDetailsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MovieDetailsViewModel(movieDetailsRepository) as T
            }
        }).get(MovieDetailsViewModel::class.java)

        movieDetailsViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when(state) {
                is MovieDetailsState.DoneState -> showDoneState(state.movie, state.isInLibrary)
                is MovieDetailsState.LoadingState -> showLoadingState()
                is MovieDetailsState.ErrorNetworkState -> showNetworkErrorState(movieId)
                is MovieDetailsState.ErrorAuthenticationState -> showAuthenticationErrorState()
                is MovieDetailsState.ErrorState -> showErrorState()
            }
        })

        addOrRemoveMovieFAB.setOnClickListener {
            movieDetailsViewModel.addOrRemoveMovie()
        }

        viewBinding.hasBeenWatchedFAB.setOnClickListener {
            movieDetailsViewModel.toogleHasBeenWatched()
        }

        movieDetailsViewModel.loadMovie(movieId)

        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showDoneState(movie: LibraryMovie, isInLibrary: Boolean) {
        loadingProgressBar.hide()
        viewBinding.titleTextView.text = movie.title
        viewBinding.releaseDateTextView.text = movie.release_date.substring(0, 4)
        viewBinding.averageVoteTextView.text = movie.vote_average.toString()
        viewBinding.genreTextView.text = movie.genres
        when(movie.runtime) {
            null -> viewBinding.runtimeTextView.visibility = View.GONE
            else -> {
                viewBinding.runtimeTextView.text = getString(R.string.movie_detail_runtime, movie.runtime)
                viewBinding.runtimeTextView.visibility = View.VISIBLE
            }
        }
        when(movie.tagline) {
            null, "" -> viewBinding.taglineTextView.visibility = View.GONE
            else -> {
                viewBinding.taglineTextView.text = "\"${movie.tagline}\""
                viewBinding.taglineTextView.visibility = View.VISIBLE
            }
        }
        when(movie.overview) {
            null, "" -> viewBinding.overviewCardView.visibility = View.GONE
            else -> {
                viewBinding.overviewTextView.text = movie.overview
                viewBinding.overviewCardView.visibility = View.VISIBLE
            }
        }
        when(isInLibrary) {
            true -> {
                viewBinding.addOrRemoveMovieFAB.isSelected = true
                viewBinding.hasBeenWatchedFAB.visibility = View.VISIBLE
                viewBinding.hasBeenWatchedFAB.isSelected = movie.has_been_watched
            }
            false -> {
                viewBinding.addOrRemoveMovieFAB.isSelected = false
                viewBinding.hasBeenWatchedFAB.visibility = View.GONE
            }
        }
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w92${movie.poster_path}")
            .resize(92, 138)
            .centerCrop()
            .transform(RoundedCornersTransformation(4, 1))
            .into(viewBinding.coverImageView)
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w780${movie.backdrop_path}")
            .fit()
            .transform(RoundedCornersTransformation(4, 1))
            .into(viewBinding.backdropImageView)
    }

    private fun showLoadingState() {
        loadingProgressBar.show()
    }

    private fun showAuthenticationErrorState() {
        loadingProgressBar.hide()

        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_error_authentication_title)
            .setMessage(R.string.dialog_error_authentication_message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                activity?.finishAndRemoveTask()
            }
            .create()
            .show()
    }

    private fun showNetworkErrorState(id: Int) {
        loadingProgressBar.hide()

        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_error_network_title)
            .setMessage(R.string.dialog_error_network_message)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_error_network_button_retry) { dialog, which ->
                movieDetailsViewModel.loadMovie(id)
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showErrorState() {
        loadingProgressBar.hide()

        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_error_title)
            .setMessage(R.string.dialog_error_message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                activity?.finishAndRemoveTask()
            }
            .create()
            .show()
    }
}