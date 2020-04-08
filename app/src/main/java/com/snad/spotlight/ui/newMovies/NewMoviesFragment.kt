package com.snad.spotlight.ui.newMovies

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.snad.spotlight.NewMoviesRepository
import com.snad.spotlight.R
import com.snad.spotlight.databinding.FragmentNewMoviesBinding
import com.snad.spotlight.network.ApiKeyInterceptor
import com.snad.spotlight.network.NewMoviesApi
import com.snad.spotlight.network.NewMoviesService
import com.snad.spotlight.network.models.ListMovie
import com.snad.spotlight.network.models.NewMovies
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class NewMoviesFragment : Fragment() {

    private lateinit var newMoviesViewModel: NewMoviesViewModel
    private var binding: FragmentNewMoviesBinding? = null
    private val viewBinding: FragmentNewMoviesBinding
        get() = binding!!

    private val movies = mutableListOf<ListMovie>()

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

        val service = retrofit.create<NewMoviesService>(NewMoviesService::class.java)
        val newMoviesApi = NewMoviesApi(service)
        val newMoviesRepository = NewMoviesRepository(newMoviesApi)

        newMoviesViewModel = ViewModelProvider(this, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return NewMoviesViewModel(newMoviesRepository) as T
            }
        }).get(NewMoviesViewModel::class.java)

        newMoviesViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when(state) {
                is NewMoviesState.DoneState -> showDoneState(state.newMovies)
                is NewMoviesState.LoadingState -> showLoadingState()
                is NewMoviesState.AuthenticationErrorState -> showAuthenticationErrorState()
                is NewMoviesState.NetworkErrorState -> showNetworkErrorState()
                is NewMoviesState.ErrorState -> showErrorState()
            }
        })

        newMoviesViewModel.loadNewMovies()

        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun movieClickListener(id: Int, coverImageView: ImageView) {
        val extras = FragmentNavigatorExtras(
            coverImageView to "cover${id}"
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