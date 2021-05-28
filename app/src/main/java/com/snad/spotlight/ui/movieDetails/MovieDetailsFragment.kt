package com.snad.spotlight.ui.movieDetails

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import androidx.transition.TransitionInflater
import com.snad.core.persistence.models.CastMember
import com.snad.core.persistence.models.Image
import com.snad.spotlight.R
import com.snad.spotlight.databinding.FragmentMovieDetailsBinding
import com.snad.spotlight.network.models.Backdrop
import com.snad.core.persistence.models.LibraryMovie
import com.snad.core.persistence.models.Review
import com.snad.spotlight.repository.MovieDetailsRepository
import com.snad.spotlight.ui.AnimationUtil
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import javax.inject.Inject

class MovieDetailsFragment: Fragment() {

    private lateinit var movieDetailsViewModel: MovieDetailsViewModel
    private var binding: FragmentMovieDetailsBinding? = null
    private val viewBinding: FragmentMovieDetailsBinding
        get() = binding!!

    private val backdrops = mutableListOf<Image>()
    private val castMember = mutableListOf<CastMember>()
    private val reviews = mutableListOf<Review>()

    @Inject
    lateinit var movieDetailsRepository: MovieDetailsRepository

    @Inject
    lateinit var viewModelFactory: MovieDetailsViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val arguments: MovieDetailsFragmentArgs by navArgs()
        val movieId = arguments.id

        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        viewBinding.coverImageView.transitionName = "cover${movieId}"

        viewBinding.backdropsRecyclerView.adapter = BackdropsAdapter(backdrops)
        viewBinding.castRecyclerView.adapter = CastAdapter(castMember, this::castClickListener)
        viewBinding.reviewsRecyclerView.adapter = ReviewsAdapter(reviews)

        inject()

        movieDetailsViewModel = ViewModelProvider(this, viewModelFactory)[MovieDetailsViewModel::class.java]

        movieDetailsViewModel.state.observe(viewLifecycleOwner) { state ->
            when(state) {
                is MovieDetailsState.DoneState -> showDoneState(state.movie, state.isInLibrary)
                is MovieDetailsState.LoadingState -> showLoadingState()
                is MovieDetailsState.ErrorNetworkState -> showNetworkErrorState(movieId)
                is MovieDetailsState.ErrorAuthenticationState -> showAuthenticationErrorState()
                is MovieDetailsState.ErrorState -> showErrorState()
            }
        }

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

    private fun castClickListener(id: Int) {
        val action = MovieDetailsFragmentDirections.actionNavigationMovieDetailsToNavigationCastDetails(
            id = id,
            backgroundColor = (viewBinding.background.background as ColorDrawable).color,
            titleColor = viewBinding.castHeadline.currentTextColor,
            bodyColor = viewBinding.runtimeTextView.currentTextColor,
            accentColor = (viewBinding.overviewBackground.background as ColorDrawable).color,
            accentBodyColor = viewBinding.overviewTextView.currentTextColor
        )
        findNavController().navigate(action)
    }

    private fun showDoneState(movie: LibraryMovie, isInLibrary: Boolean) {
        viewBinding.loadingProgressbar.hide()
        setViewsVisibility(View.VISIBLE)
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w154${movie.poster_path}")
            .resize(154, 231)
            .centerCrop()
            .placeholder(R.drawable.cover_image_placeholder)
            .error(R.drawable.cover_image_error)
            .transform(RoundedCornersTransformation(4, 1))
            .into(viewBinding.coverImageView)
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w780${movie.backdrop_path}")
            .resize(780, 439)
            .centerCrop()
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
                AnimationUtil.startMaterialFade(requireContext(), viewBinding.layoutRoot)
                viewBinding.hasBeenWatchedFAB.visibility = View.VISIBLE
                viewBinding.addOrRemoveMovieFAB.isSelected = true
                viewBinding.hasBeenWatchedFAB.isSelected = movie.has_been_watched
            }
            false -> {
                AnimationUtil.startMaterialFade(requireContext(), viewBinding.layoutRoot, false)
                viewBinding.hasBeenWatchedFAB.visibility = View.GONE
                viewBinding.addOrRemoveMovieFAB.isSelected = false
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

        if(movie.backdrops.isNotEmpty()) {
            viewBinding.backdropsRecyclerView.visibility = View.VISIBLE
            backdrops.clear()
            backdrops.addAll(movie.backdrops)
            viewBinding.backdropsRecyclerView.adapter?.notifyDataSetChanged()
        }
        else {
            viewBinding.backdropsRecyclerView.visibility = View.GONE
        }

        if(movie.cast.isNotEmpty()) {
            viewBinding.castRecyclerView.visibility = View.VISIBLE
            viewBinding.castHeadline.visibility = View.VISIBLE
            castMember.clear()
            castMember.addAll(movie.cast)
            viewBinding.castRecyclerView.adapter?.notifyDataSetChanged()
        }
        else {
            viewBinding.castRecyclerView.visibility = View.GONE
            viewBinding.castHeadline.visibility = View.GONE
        }

        if(movie.reviews.isNotEmpty()) {
            viewBinding.reviewsRecyclerView.visibility = View.VISIBLE
            viewBinding.reviewsHeadline.visibility = View.VISIBLE
            reviews.clear()
            reviews.addAll(movie.reviews)
            viewBinding.reviewsRecyclerView.adapter?.notifyDataSetChanged()
        }
        else {
            viewBinding.reviewsRecyclerView.visibility = View.GONE
            viewBinding.reviewsHeadline.visibility = View.GONE
        }
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

                        if((viewBinding.background.background as? ColorDrawable)?.color == Color.WHITE) {
                            AnimationUtil.circularRevealAnimation(viewBinding.background) {
                                viewBinding.background.setBackgroundColor(primarySwatch.rgb)
                            }
                        }
                        viewBinding.runtimeTextView.setTextColor(primarySwatch.bodyTextColor)
                        viewBinding.runtimeDivider.setBackgroundColor(primarySwatch.bodyTextColor)
                        viewBinding.genreTextView.setTextColor(primarySwatch.bodyTextColor)
                        viewBinding.averageVoteTextView.setTextColor(primarySwatch.bodyTextColor)
                        viewBinding.averageVoteImageView.setColorFilter(primarySwatch.bodyTextColor)
                        viewBinding.taglineTextView.setTextColor(primarySwatch.bodyTextColor)
                        if((viewBinding.overviewBackground.background as? ColorDrawable)?.color == Color.WHITE) {
                            AnimationUtil.circularRevealAnimation(viewBinding.overviewBackground) {
                                viewBinding.overviewBackground.setBackgroundColor(accentSwatch.rgb)
                                viewBinding.overviewHeadlineTextView.setTextColor(accentSwatch.titleTextColor)
                                viewBinding.overviewTextView.setTextColor(accentSwatch.bodyTextColor)
                            }
                        }
                        viewBinding.castHeadline.setTextColor(primarySwatch.bodyTextColor)
                        viewBinding.reviewsHeadline.setTextColor(primarySwatch.bodyTextColor)
                        viewBinding.trailerFAB.backgroundTintList = ColorStateList.valueOf(accentSwatch.rgb)
                        viewBinding.trailerFAB.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.movieDetailTrailerIconColorTinted, null))

                        viewBinding.addOrRemoveMovieFAB.backgroundTintList =
                            if(isInLibrary) ColorStateList.valueOf(accentSwatch.rgb)
                            else ColorStateList.valueOf(resources.getColor(R.color.movieDetailFAB, null))

                        viewBinding.hasBeenWatchedFAB.backgroundTintList =
                            if(hasBeenWatched) ColorStateList.valueOf(accentSwatch.rgb)
                            else ColorStateList.valueOf(resources.getColor(R.color.movieDetailFAB, null))

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
        setViewsVisibility(View.INVISIBLE)
    }

    private fun showAuthenticationErrorState() {
        viewBinding.loadingProgressbar.hide()
        setViewsVisibility(View.INVISIBLE)

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
        setViewsVisibility(View.INVISIBLE)

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
        setViewsVisibility(View.INVISIBLE)

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

    private fun setViewsVisibility(visibility: Int) {
        viewBinding.releaseDateTextView.visibility = visibility
        viewBinding.runtimeTextView.visibility = visibility
        viewBinding.runtimeDivider.visibility = visibility
        viewBinding.genreTextView.visibility = visibility
        viewBinding.hasBeenWatchedFAB.visibility = visibility
        viewBinding.addOrRemoveMovieFAB.visibility = visibility
        viewBinding.reviewsHeadline.visibility = visibility
        viewBinding.castHeadline.visibility = visibility
        viewBinding.trailerFAB.visibility = visibility
        viewBinding.backdropFilter.visibility = visibility
        viewBinding.averageVoteImageView.visibility = visibility
        viewBinding.averageVoteTextView.visibility = visibility
        viewBinding.overviewCardView.visibility = visibility
    }
}