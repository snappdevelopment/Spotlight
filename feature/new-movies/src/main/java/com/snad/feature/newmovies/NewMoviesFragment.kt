package com.snad.feature.newmovies

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.snad.core.arch.observeWithLifecycle
import com.snad.feature.newmovies.repository.NewMoviesRepositoryImpl
import com.snad.feature.newmovies.databinding.FragmentNewMoviesBinding
import com.snad.feature.newmovies.model.ListMovie
import com.snad.feature.newmovies.model.NewMovies
import javax.inject.Inject


class NewMoviesFragment : Fragment() {

    private val newMoviesViewModel: NewMoviesViewModel by viewModels { viewModelFactory }
    private var binding: FragmentNewMoviesBinding? = null
    private val viewBinding: FragmentNewMoviesBinding
        get() = binding!!

    private val movies = mutableListOf<ListMovie>()

    internal lateinit var newMoviesComponent: NewMoviesComponent

    @Inject
    internal lateinit var newMoviesRepository: NewMoviesRepositoryImpl

    @Inject
    internal lateinit var viewModelFactory: NewMoviesViewModel.Factory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimaryDark, null)
        binding = FragmentNewMoviesBinding.inflate(inflater, container, false)

        viewBinding.recyclerView.adapter = NewMoviesAdapter(
            movies,
            this::movieClickListener
        )

        inject()

        newMoviesViewModel.state.observeWithLifecycle(this) { state ->
            when (state) {
                is NewMoviesState.DoneState -> showDoneState(state.newMovies)
                is NewMoviesState.LoadingState -> showLoadingState()
                is NewMoviesState.AuthenticationErrorState -> showAuthenticationErrorState()
                is NewMoviesState.NetworkErrorState -> showNetworkErrorState()
                is NewMoviesState.ErrorState -> showErrorState()
            }
        }

        newMoviesViewModel.handleAction(LoadMovies)

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
                newMoviesViewModel.handleAction(LoadMovies)
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