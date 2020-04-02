package com.snad.spotlight.ui.search

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.snad.spotlight.R
import com.snad.spotlight.SearchRepository
import com.snad.spotlight.databinding.FragmentSearchBinding
import com.snad.spotlight.network.ApiKeyInterceptor
import com.snad.spotlight.network.MovieSearchApi
import com.snad.spotlight.network.SearchService
import com.snad.spotlight.network.models.ListMovie
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*


class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private var binding: FragmentSearchBinding? = null
    private val viewBinding: FragmentSearchBinding
        get() = binding!!

    private val movies = mutableListOf<ListMovie>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimaryDark, null)
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        viewBinding.recyclerView.adapter = SearchAdapter(
            movies,
            this::movieClickListener
        )

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

        val service = retrofit.create<SearchService>(SearchService::class.java)
        val movieSearchApi = MovieSearchApi(service)
        val searchRepository = SearchRepository(movieSearchApi)

        searchViewModel = ViewModelProvider(this, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel(searchRepository) as T
            }
        }).get(SearchViewModel::class.java)

        searchViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when(state) {
                is SearchState.DoneState -> showDoneState(state.searchResults)
                is SearchState.InitialState -> showInitialState() //"Search for movies (with Icon)"
                is SearchState.NoResultsState -> showNoResultsState() //"Couldn't find any movies"
                is SearchState.LoadingState -> showLoadingState()
                is SearchState.AuthenticationErrorState -> showAuthenticationErrorState()
                is SearchState.NetworkErrorState -> showNetworkErrorState()
                is SearchState.ErrorState -> showErrorState()
            }
        })

        viewBinding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null && query != "") searchViewModel.searchMovies(query)
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

    private fun movieClickListener(id: Int) {
        val bundle = Bundle()
        bundle.putInt("id", id)
        findNavController().navigate(R.id.action_navigation_search_to_navigation_movie_details, bundle)
    }

    private fun showDoneState(moviesList: List<ListMovie>) {
        viewBinding.loadingProgressbar.hide()
        viewBinding.statusTextView.visibility = View.GONE
        viewBinding.statusIconImageView.visibility = View.GONE
        viewBinding.recyclerView.visibility = View.VISIBLE

        movies.clear()
        movies.addAll(moviesList)
        viewBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun showLoadingState() {
        viewBinding.recyclerView.visibility = View.INVISIBLE
        viewBinding.statusTextView.visibility = View.GONE
        viewBinding.statusIconImageView.visibility = View.GONE
        viewBinding.loadingProgressbar.show()
    }

    private fun showInitialState() {
        viewBinding.loadingProgressbar.hide()
        viewBinding.recyclerView.visibility = View.INVISIBLE
        viewBinding.statusTextView.text = getString(R.string.search_initial_state)
        viewBinding.statusTextView.visibility = View.VISIBLE
        viewBinding.statusIconImageView.setImageResource(R.drawable.ic_video_vintage)
        viewBinding.statusIconImageView.visibility = View.VISIBLE
    }

    private fun showNoResultsState() {
        viewBinding.loadingProgressbar.hide()
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
                    searchViewModel.searchMovies(viewBinding.searchView.query.toString())
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
