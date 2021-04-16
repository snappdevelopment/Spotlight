package com.snad.spotlight.ui.newMovies

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.snad.spotlight.App
import com.snad.spotlight.repository.NewMoviesRepository
import com.snad.spotlight.R
import com.snad.spotlight.databinding.FragmentNewMoviesBinding
import com.snad.spotlight.network.ApiKeyInterceptor
import com.snad.spotlight.network.NewMoviesApi
import com.snad.spotlight.network.NewMoviesService
import com.snad.spotlight.network.RetrofitClient
import com.snad.spotlight.network.models.ListMovie
import com.snad.spotlight.network.models.NewMovies
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject


class NewMoviesFragment : Fragment() {

    private lateinit var newMoviesViewModel: NewMoviesViewModel
    private var binding: FragmentNewMoviesBinding? = null
    private val viewBinding: FragmentNewMoviesBinding
        get() = binding!!

    private val movies = mutableListOf<ListMovie>()

    @Inject
    lateinit var viewModelFactory: NewMoviesViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        newMoviesViewModel = ViewModelProvider(this, viewModelFactory)[NewMoviesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimaryDark, null)
        binding = FragmentNewMoviesBinding.inflate(inflater, container, false)

        viewBinding.recyclerView.adapter = NewMoviesAdapter(
            movies,
            this::movieClickListener
        )

        newMoviesViewModel.state.observe(viewLifecycleOwner) {
            when(it) {
                is NewMoviesState.DoneState -> showDoneState(it.newMovies)
                is NewMoviesState.LoadingState -> showLoadingState()
                is NewMoviesState.AuthenticationErrorState -> showAuthenticationErrorState()
                is NewMoviesState.NetworkErrorState -> showNetworkErrorState()
                is NewMoviesState.ErrorState -> showErrorState()
            }
        }

        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun movieClickListener(id: Int, coverCardView: CardView) {
        val extras = FragmentNavigatorExtras(
            coverCardView to "cover${id}"
        )
        val action = NewMoviesFragmentDirections.actionNavigationNewMoviesToNavigationMovieDetails(id)
        findNavController().navigate(action, extras)
    }

    private fun showDoneState(newMovies: NewMovies) {
        viewBinding.loadingProgressbar.hide()

        movies.clear()
        movies.addAll(newMovies.movies)
        viewBinding.recyclerView.adapter?.notifyDataSetChanged()
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

    private fun showNetworkErrorState() {
        viewBinding.loadingProgressbar.hide()

        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_error_network_title)
            .setMessage(R.string.dialog_error_network_message)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_error_network_button_retry) { dialog, which ->
                newMoviesViewModel.loadNewMovies()
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