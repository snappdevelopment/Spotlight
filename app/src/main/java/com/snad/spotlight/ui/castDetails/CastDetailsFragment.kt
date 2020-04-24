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

class CastDetailsFragment : Fragment() {

    private lateinit var castDetailsViewModel: CastDetailsViewModel
    private var binding: FragmentCastDetailsBinding? = null
    private val viewBinding: FragmentCastDetailsBinding
        get() = binding!!

    private val knownFor = mutableListOf<Cast>()

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

        val personService = retrofit.create<PersonService>(PersonService::class.java)
        val personApi = PersonApi(personService)
        val personRepository = PersonRepository(personApi)

        castDetailsViewModel = ViewModelProvider(this, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CastDetailsViewModel(personRepository) as T
            }
        }).get(CastDetailsViewModel::class.java)

        castDetailsViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when(state) {
                is CastDetailsState.DoneState -> showDoneState(state.person)
                is CastDetailsState.LoadingState -> showLoadingState()
                is CastDetailsState.AuthenticationErrorState -> showAuthenticationErrorState()
                is CastDetailsState.NetworkErrorState -> showNetworkErrorState(castId)
                is CastDetailsState.ErrorState -> showErrorState()
            }
        })

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
        if(person.birthday != null && person.birthday.isNotEmpty()) {
            val birthdayDate = SimpleDateFormat("yyyy-MM-dd").parse(person.birthday)
            val localBirthdayDate = DateFormat.getDateInstance(SimpleDateFormat.SHORT).format(birthdayDate!!)
            viewBinding.birthdayTextView.text = localBirthdayDate
            if(person.deathday != null && person.deathday.isNotEmpty()) {
                val deathdayDate = SimpleDateFormat("yyyy-MM-dd").parse(person.deathday)
                val localDeathdayDate = DateFormat.getDateInstance(SimpleDateFormat.SHORT).format(deathdayDate!!)
                viewBinding.deathdayTextView.text = localDeathdayDate
            }
            else {
                viewBinding.birthdayDashView.visibility = View.GONE
                viewBinding.deathdayTextView.visibility = View.GONE
            }
        }
        else {
            viewBinding.birthdayTextView.visibility = View.GONE
            viewBinding.birthdayDashView.visibility = View.GONE
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

    private fun setViewsVisibility(visibility: Int) {
        viewBinding.profileCardView.visibility = visibility
        viewBinding.nameTextView.visibility = visibility
        viewBinding.birthdayTextView.visibility = visibility
        viewBinding.birthdayDashView.visibility = visibility
        viewBinding.deathdayTextView.visibility = visibility
        viewBinding.birthplaceHeaderTextView.visibility = visibility
        viewBinding.birthplaceTextView.visibility = visibility
        viewBinding.bioHeadlineTextView.visibility = visibility
        viewBinding.bioCardView.visibility = visibility
        viewBinding.knownForHeadlineTextView.visibility = visibility
        viewBinding.knownForRecyclerView.visibility = visibility
    }

}
