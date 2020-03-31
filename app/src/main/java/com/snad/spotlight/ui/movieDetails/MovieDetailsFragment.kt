package com.snad.spotlight.ui.movieDetails

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.snad.spotlight.*
import com.snad.spotlight.databinding.FragmentMovieDetailsBinding
import com.snad.spotlight.network.ApiKeyInterceptor
import com.snad.spotlight.network.MovieApi
import com.snad.spotlight.network.MovieService
import com.snad.spotlight.network.models.Backdrop
import com.snad.spotlight.network.models.CastMember
import com.snad.spotlight.network.models.Review
import com.snad.spotlight.persistence.LibraryDb
import com.snad.spotlight.persistence.models.LibraryMovie
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_movie_details.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MovieDetailsFragment: Fragment() {

    private lateinit var movieDetailsViewModel: MovieDetailsViewModel
    private var binding: FragmentMovieDetailsBinding? = null
    private val viewBinding: FragmentMovieDetailsBinding
        get() = binding!!

    private val backdrops = mutableListOf<Backdrop>()
    private val castMember = mutableListOf<CastMember>()
    private val reviews = mutableListOf<Review>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val movieId = arguments?.getInt("id")
        movieId ?: throw NullPointerException("MovieId is null")

//        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        requireActivity().window.attributes.flags = requireActivity().window.attributes.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//        requireActivity().window.statusBarColor = Color.TRANSPARENT

//        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
//        activity?.window?.statusBarColor = Color.TRANSPARENT

//        activity?.transparentStatusBarEnabled(true)
//        activity?.setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
//        activity?.setWindowFlag(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, true)
//        activity?.setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true)
//        activity?.window?.statusBarColor = Color.TRANSPARENT

        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        viewBinding.backdropsRecyclerView.adapter = BackdropsAdapter(backdrops)
        viewBinding.castRecyclerView.adapter = CastAdapter(castMember)
        viewBinding.reviewsRecyclerView.adapter = ReviewsAdapter(reviews)

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

        viewBinding.addOrRemoveMovieFAB.setOnClickListener {
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
        viewBinding.loadingProgressbar.hide()
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w92${movie.poster_path}")
            .resize(92, 138)
            .centerCrop()
            .placeholder(R.drawable.cover_image_placeholder)
            .error(R.drawable.cover_image_error)
            .transform(RoundedCornersTransformation(4, 1))
            .into(viewBinding.coverImageView)
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w780${movie.backdrop_path}")
            .fit()
            .transform(RoundedCornersTransformation(4, 1))
            .into(viewBinding.backdropImageView, object: Callback {
                override fun onSuccess() {
                    setColorPalette(isInLibrary, movie.has_been_watched)
                }

                override fun onError(e: Exception?) {}
            })
        viewBinding.titleTextView.text = movie.title
        viewBinding.averageVoteTextView.text = movie.vote_average.toString()
        viewBinding.runtimeDivider.visibility = if(movie.genres == "") View.INVISIBLE else View.VISIBLE
        viewBinding.genreTextView.text = movie.genres
        if(movie.release_date == "") viewBinding.releaseDateTextView.visibility = View.GONE
        else viewBinding.releaseDateTextView.text = movie.release_date.substring(0, 4)
        if(movie.tagline != null && movie.tagline != "") viewBinding.taglineTextView.text = "\"${movie.tagline}\""
        else viewBinding.taglineTextView.text = ""
        when(movie.runtime) {
            null -> viewBinding.runtimeTextView.visibility = View.GONE
            else -> {
                viewBinding.runtimeTextView.text = getString(R.string.movie_detail_runtime, movie.runtime)
                viewBinding.runtimeTextView.visibility = View.VISIBLE
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
        when(movie.trailer) {
            null -> viewBinding.trailerFAB.visibility = View.INVISIBLE
            else -> {
                viewBinding.trailerFAB.visibility = View.VISIBLE
                viewBinding.trailerFAB.setOnClickListener { view ->
                    try {
                        val appIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("vnd.youtube:${movie.trailer}"))
                        context?.startActivity(appIntent)
                    } catch (e: ActivityNotFoundException) {
                        val webIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=${movie.trailer}")
                        )
                        context?.startActivity(webIntent)
                    }
                }
            }
        }

        backdrops.clear()
        backdrops.addAll(movie.backdrops)
        viewBinding.backdropsRecyclerView.adapter?.notifyDataSetChanged()

        castMember.clear()
        castMember.addAll(movie.cast)
        viewBinding.castRecyclerView.adapter?.notifyDataSetChanged()

        reviews.clear()
        reviews.addAll(movie.reviews)
        viewBinding.reviewsRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun setColorPalette(isInLibrary: Boolean, hasBeenWatched: Boolean) {
        val bitmap = viewBinding.backdropImageView.drawable.toBitmap()
        Palette.from(bitmap)
            .maximumColorCount(24)
            .generate { palette ->
                if (palette != null) {
                    val primarySwatch = palette.dominantSwatch
                    var accentSwatch = palette.lightMutedSwatch

                    if(accentSwatch == null || accentSwatch == primarySwatch) {
                        accentSwatch = palette.mutedSwatch
                        if (accentSwatch == null || accentSwatch == primarySwatch) {
                            accentSwatch = palette.vibrantSwatch
                        }
                    }

                    if (primarySwatch != null && accentSwatch != null) {
                        viewBinding.scrollView.setBackgroundColor(primarySwatch.rgb)
                        viewBinding.backdropFilter.setBackgroundColor(primarySwatch.rgb)
                        viewBinding.runtimeTextView.setTextColor(primarySwatch.bodyTextColor)
                        viewBinding.runtimeDivider.setBackgroundColor(primarySwatch.bodyTextColor)
                        viewBinding.genreTextView.setTextColor(primarySwatch.bodyTextColor)
                        viewBinding.averageVoteTextView.setTextColor(primarySwatch.bodyTextColor)
                        viewBinding.averageVoteImageView.setColorFilter(primarySwatch.bodyTextColor)
                        viewBinding.taglineTextView.setTextColor(primarySwatch.bodyTextColor)
                        viewBinding.overviewCardView.setCardBackgroundColor(accentSwatch.rgb)
                        viewBinding.overviewHeadlineTextView.setTextColor(accentSwatch.titleTextColor)
                        viewBinding.overviewTextView.setTextColor(accentSwatch.bodyTextColor)
                        viewBinding.trailerFAB.backgroundTintList = ColorStateList.valueOf(accentSwatch.rgb)
                        viewBinding.trailerFAB.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.movieDetailTrailerIconColorTinted, null))

                        viewBinding.addOrRemoveMovieFAB.backgroundTintList =
                            if(isInLibrary) ColorStateList.valueOf(accentSwatch.rgb) else ColorStateList.valueOf(resources.getColor(R.color.movieDetailFAB, null))

                        viewBinding.hasBeenWatchedFAB.backgroundTintList =
                            if(hasBeenWatched) ColorStateList.valueOf(accentSwatch.rgb) else ColorStateList.valueOf(resources.getColor(R.color.movieDetailFAB, null))

                        val castAdapter = viewBinding.castRecyclerView.adapter as CastAdapter
                        castAdapter.nameTextColor = accentSwatch.titleTextColor
                        castAdapter.nameBackgroundColor = accentSwatch.rgb
                        castAdapter.notifyDataSetChanged()

                        val reviewsAdapter = viewBinding.reviewsRecyclerView.adapter as ReviewsAdapter
                        reviewsAdapter.cardColor = accentSwatch.rgb
                        reviewsAdapter.authorTextColor = accentSwatch.titleTextColor
                        reviewsAdapter.reviewTextColor = accentSwatch.bodyTextColor
                        reviewsAdapter.notifyDataSetChanged()

                        activity?.window?.statusBarColor = primarySwatch.rgb
                    }
                }
            }
    }

    private fun showLoadingState() {
        viewBinding.loadingProgressbar.show()
    }

    private fun showAuthenticationErrorState() {
        viewBinding.loadingProgressbar.hide()

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
        viewBinding.loadingProgressbar.hide()

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
        viewBinding.loadingProgressbar.hide()

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