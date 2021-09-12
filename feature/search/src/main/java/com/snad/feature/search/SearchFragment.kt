package com.snad.feature.search

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.snad.core.arch.observeWithLifecycle
import com.snad.feature.search.repository.SearchRepository
import com.snad.feature.search.databinding.FragmentSearchBinding
import com.snad.feature.search.model.ListMovie
import java.util.*
import javax.inject.Inject


class SearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by viewModels { viewModelFactory }
    private var binding: FragmentSearchBinding? = null
    private val viewBinding: FragmentSearchBinding
        get() = binding!!

    private val movies = mutableListOf<ListMovie>()

    internal lateinit var searchComponent: SearchComponent

    @Inject
    internal lateinit var searchRepository: SearchRepository

    @Inject
    internal lateinit var viewModelFactory: SearchViewModel.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimaryDark, null)
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        viewBinding.recyclerView.adapter = SearchAdapter(
            movies,
            this::movieClickListener
        )

        inject()

        searchViewModel.state.observeWithLifecycle(this) { state ->
            when(state) {
                is SearchState.DoneState -> showDoneState(state.searchResults)
                is SearchState.InitialState -> showInitialState() //"Search for movies (with Icon)"
                is SearchState.NoResultsState -> showNoResultsState() //"Couldn't find any movies"
                is SearchState.LoadingState -> showLoadingState()
                is SearchState.AuthenticationErrorState -> showAuthenticationErrorState()
                is SearchState.NetworkErrorState -> showNetworkErrorState()
                is SearchState.ErrorState -> showErrorState()
            }
        }

        viewBinding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null && query != "") searchViewModel.handleAction(SearchMovies(query))
                viewBinding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {return true}
        })

        viewBinding.searchView.requestFocus()
        showKeyboard()

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
        val action = SearchFragmentDirections.actionNavigationSearchToNavigationMovieDetails(id)
        findNavController().navigate(action, extras)
    }

    private fun showDoneState(moviesList: List<ListMovie>) {
        viewBinding.loadingProgressbar.hide()
        viewBinding.headerBackground.pivotY = 0f
        viewBinding.headerBackground.scaleY = 1f
        viewBinding.statusTextView.visibility = View.GONE
        viewBinding.statusIconImageView.visibility = View.GONE
        viewBinding.recyclerView.visibility = View.VISIBLE

        movies.clear()
        movies.addAll(moviesList)
        viewBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun showLoadingState() {
        viewBinding.headerBackground.pivotY = 0f
        viewBinding.headerBackground.scaleY = 0.75f
        viewBinding.recyclerView.visibility = View.INVISIBLE
        viewBinding.statusTextView.visibility = View.GONE
        viewBinding.statusIconImageView.visibility = View.GONE
        viewBinding.loadingProgressbar.show()
    }

    private fun showInitialState() {
        viewBinding.loadingProgressbar.hide()
        viewBinding.headerBackground.pivotY = 0f
        viewBinding.headerBackground.scaleY = 0.75f
        viewBinding.recyclerView.visibility = View.INVISIBLE
        viewBinding.statusTextView.text = getString(R.string.search_initial_state)
        viewBinding.statusTextView.visibility = View.VISIBLE
        viewBinding.statusIconImageView.setImageResource(R.drawable.ic_video_vintage)
        viewBinding.statusIconImageView.visibility = View.VISIBLE
    }

    private fun showNoResultsState() {
        viewBinding.loadingProgressbar.hide()
        viewBinding.headerBackground.pivotY = 0f
        viewBinding.headerBackground.scaleY = 0.75f
        viewBinding.recyclerView.visibility = View.INVISIBLE
        viewBinding.statusTextView.text = getString(R.string.search_no_results_state)
        viewBinding.statusTextView.visibility = View.VISIBLE
        viewBinding.statusIconImageView.setImageResource(R.drawable.ic_emoticon_sad_outline)
        viewBinding.statusIconImageView.visibility = View.VISIBLE
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
                if(viewBinding.searchView.query != null && viewBinding.searchView.query != "") {
                    searchViewModel.handleAction(SearchMovies(viewBinding.searchView.query.toString()))
                }
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

    private fun showKeyboard() {
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                val inputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
        val timer = Timer()
        timer.schedule(timerTask, 500)
    }
}
