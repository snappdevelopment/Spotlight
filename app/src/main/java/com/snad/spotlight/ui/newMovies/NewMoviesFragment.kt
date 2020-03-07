package com.snad.spotlight.ui.newMovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.snad.spotlight.NewMoviesRepository
import com.snad.spotlight.databinding.FragmentNewMoviesBinding
import com.snad.spotlight.network.NewMoviesApi
import com.snad.spotlight.network.NewMoviesService
import com.snad.spotlight.network.models.NewMovie
import com.snad.spotlight.network.models.NewMovies
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class NewMoviesFragment : Fragment() {

    private lateinit var newMoviesViewModel: NewMoviesViewModel
    private var binding: FragmentNewMoviesBinding? = null
    private val viewBinding: FragmentNewMoviesBinding
        get() = binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: NewMoviesAdapter
    private val movies = mutableListOf<NewMovie>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewMoviesBinding.inflate(inflater, container, false)

        recyclerView = viewBinding.recyclerView
        recyclerViewAdapter = NewMoviesAdapter(movies)
        recyclerView.adapter = recyclerViewAdapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
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
                is NewMoviesState.DoneState -> updateRecyclerView(state.newMovies)
            }
            //Todo: handle states
        })

        newMoviesViewModel.loadNewMovies()

        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun updateRecyclerView(newMovies: NewMovies) {
        val moviesList = newMovies.movies
        movies.clear()
        movies.addAll(moviesList)
        recyclerViewAdapter.notifyDataSetChanged()
    }
}