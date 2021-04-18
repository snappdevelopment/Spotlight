package com.snad.spotlight.ui.castDetails

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.snad.spotlight.App
import com.snad.spotlight.R
import com.snad.spotlight.databinding.FragmentCastDetailsBinding
import com.snad.spotlight.network.ApiKeyInterceptor
import com.snad.spotlight.network.MovieService
import com.snad.spotlight.network.PersonApi
import com.snad.spotlight.network.PersonService
import com.snad.spotlight.network.models.Cast
import com.snad.spotlight.network.models.Person
import com.snad.spotlight.repository.PersonRepository
import com.snad.spotlight.ui.library.LibraryViewModel
import com.snad.spotlight.ui.movieDetails.MovieDetailsFragmentArgs
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_cast_details.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CastDetailsFragment : Fragment() {

    private lateinit var castDetailsViewModel: CastDetailsViewModel
    private var binding: FragmentCastDetailsBinding? = null
    private val viewBinding: FragmentCastDetailsBinding
        get() = binding!!

    private val knownFor = mutableListOf<Cast>()

    @Inject
    lateinit var personRepository: PersonRepository

    @Inject
    lateinit var viewModelFactory: CastDetailsViewModel.Factory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val arguments: CastDetailsFragmentArgs by navArgs()
        val castId = arguments.id

        binding = FragmentCastDetailsBinding.inflate(inflater, container, false)

        val knownForAdapter = KnownForAdapter(knownFor)
        viewBinding.knownForRecyclerView.adapter = knownForAdapter

        setColorPalette(arguments.backgroundColor, arguments.titleColor, arguments.bodyColor, arguments.accentColor, arguments.accentBodyColor)

        inject()

        castDetailsViewModel = ViewModelProvider(this, viewModelFactory)[CastDetailsViewModel::class.java]

        castDetailsViewModel.state.observe(viewLifecycleOwner) { state ->
            when(state) {
                is CastDetailsState.DoneState -> showDoneState(state.person)
                is CastDetailsState.LoadingState -> showLoadingState()
                is CastDetailsState.AuthenticationErrorState -> showAuthenticationErrorState()
                is CastDetailsState.NetworkErrorState -> showNetworkErrorState(castId)
                is CastDetailsState.ErrorState -> showErrorState()
            }
        }

        castDetailsViewModel.loadCastDetails(castId)

        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showDoneState(person: Person) {
        viewBinding.loadingProgressbar.hide()
        setViewsVisibility(View.VISIBLE)
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w185${person.profile_path}")
            .resize(185, 278)
            .centerCrop()
            .placeholder(R.drawable.cover_image_placeholder)
            .error(R.drawable.cover_image_error)
            .transform(RoundedCornersTransformation(4, 1))
            .into(viewBinding.profileImageView)
        viewBinding.nameTextView.text = person.name
        if(person.birthday != null) {
            viewBinding.birthdayTextView.text = person.birthday
            if(person.deathday != null) {
                viewBinding.deathdayTextView.text = person.deathday
            }
            else {
                viewBinding.deathdayHeaderTextView.visibility = View.GONE
                viewBinding.deathdayTextView.visibility = View.GONE
            }
        }
        else {
            viewBinding.birthdayTextView.visibility = View.GONE
            viewBinding.birthdayHeaderTextView.visibility = View.GONE
            viewBinding.deathdayHeaderTextView.visibility = View.GONE
            viewBinding.deathdayTextView.visibility = View.GONE
        }
        if(person.place_of_birth != null && person.place_of_birth.isNotEmpty()) {
            viewBinding.birthplaceTextView.text = person.place_of_birth
        }
        else {
            viewBinding.birthplaceTextView.visibility = View.GONE
            viewBinding.birthplaceHeaderTextView.visibility = View.GONE
        }
        if(person.biography.isNotEmpty()) {
            viewBinding.bioTextView.text = person.biography
        }
        else {
            viewBinding.bioCardView.visibility = View.GONE
            viewBinding.bioHeadlineTextView.visibility = View.GONE
        }
        if(person.person_credits.cast.isNotEmpty()) {
            knownFor.clear()
            knownFor.addAll(person.person_credits.cast)
            viewBinding.knownForRecyclerView.adapter?.notifyDataSetChanged()
        }
        else {
            viewBinding.knownForHeadlineTextView.visibility = View.GONE
            viewBinding.knownForRecyclerView.visibility = View.GONE
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
                castDetailsViewModel.loadCastDetails(id)
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

    private fun setColorPalette(
        backgroundColor: Int,
        titleColor: Int,
        bodyColor: Int,
        accentColor: Int,
        accentBodyColor: Int
    ) {
        viewBinding.scrollView.setBackgroundColor(backgroundColor)
        viewBinding.nameTextView.setTextColor(titleColor)
        viewBinding.birthdayHeaderTextView.setTextColor(bodyColor)
        viewBinding.birthdayTextView.setTextColor(bodyColor)
        viewBinding.deathdayHeaderTextView.setTextColor(bodyColor)
        viewBinding.deathdayTextView.setTextColor(bodyColor)
        viewBinding.birthplaceHeaderTextView.setTextColor(bodyColor)
        viewBinding.birthplaceTextView.setTextColor(bodyColor)
        viewBinding.bioHeadlineTextView.setTextColor(titleColor)
        viewBinding.bioTextView.setTextColor(accentBodyColor)
        viewBinding.bioCardView.setCardBackgroundColor(accentColor)
        viewBinding.knownForHeadlineTextView.setTextColor(titleColor)

        val knownForAdapter = viewBinding.knownForRecyclerView.adapter as KnownForAdapter
        knownForAdapter.cardBackgroundColor = accentColor
        knownForAdapter.textColor = accentBodyColor
        knownForAdapter.notifyDataSetChanged()

        activity?.window?.statusBarColor = backgroundColor
    }

    private fun setViewsVisibility(visibility: Int) {
        viewBinding.profileCardView.visibility = visibility
        viewBinding.nameTextView.visibility = visibility
        viewBinding.birthdayHeaderTextView.visibility = visibility
        viewBinding.birthdayTextView.visibility = visibility
        viewBinding.deathdayHeaderTextView.visibility = visibility
        viewBinding.deathdayTextView.visibility = visibility
        viewBinding.birthplaceHeaderTextView.visibility = visibility
        viewBinding.birthplaceTextView.visibility = visibility
        viewBinding.bioHeadlineTextView.visibility = visibility
        viewBinding.bioCardView.visibility = visibility
        viewBinding.knownForHeadlineTextView.visibility = visibility
        viewBinding.knownForRecyclerView.visibility = visibility
    }

}
