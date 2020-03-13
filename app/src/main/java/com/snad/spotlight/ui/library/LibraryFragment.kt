package com.snad.spotlight.ui.library

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.snad.spotlight.App
import com.snad.spotlight.LibraryRepository
import com.snad.spotlight.R
import com.snad.spotlight.databinding.FragmentLibraryBinding
import com.snad.spotlight.persistence.LibraryDb
import com.snad.spotlight.persistence.models.LibraryMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class LibraryFragment : Fragment() {

    private lateinit var libraryViewModel: LibraryViewModel
    private var binding: FragmentLibraryBinding? = null
    private val viewBinding: FragmentLibraryBinding
        get() = binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: LibraryAdapter
    private lateinit var loadingProgressBar: ContentLoadingProgressBar
    private val movies = mutableListOf<LibraryMovie>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)

        loadingProgressBar = viewBinding.loadingProgressbar
        recyclerView = viewBinding.recyclerView
        recyclerViewAdapter = LibraryAdapter(movies)
        recyclerView.adapter = recyclerViewAdapter

        val app = context!!.applicationContext as App
        val libraryDb = LibraryDb(app.appDb)
        val libraryRepository = LibraryRepository(libraryDb)

        libraryViewModel = ViewModelProvider(this, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return LibraryViewModel(libraryRepository) as T
            }
        }).get(LibraryViewModel::class.java)

        libraryViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when(state) {
                is LibraryState.DoneState -> showDoneState(state.libraryMovies)
                is LibraryState.LoadingState -> showLoadingState()
                is LibraryState.ErrorState -> showErrorState()
            }
        })

//        val fakeMovie = LibraryMovie(1111,
//            Calendar.getInstance(),
//            false,
//            false,
//            "",
//            100000,
//            "Drama, Comedy",
//            null,
//            null,
//            5.2,
//            null,
//            "2020.02.14",
//            2000000,
//            180,
//            null,
//            "Fake Movie",
//            false,
//            8.2,
//            5)
//        lifecycleScope.launch(Dispatchers.IO) {
//            libraryDb.insertMovie(fakeMovie)
//        }

        libraryViewModel.loadLibraryMovies()



        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showDoneState(libraryMovies: List<LibraryMovie>) {
        loadingProgressBar.hide()

        movies.clear()
        movies.addAll(libraryMovies)
        recyclerViewAdapter.notifyDataSetChanged()
    }

    private fun showLoadingState() {
        loadingProgressBar.show()
    }

    private fun showErrorState() {
        loadingProgressBar.hide()

        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_wrong_operation_title)
            .setMessage(R.string.dialog_wrong_operation_message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                activity?.finishAndRemoveTask()
            }
            .create()
            .show()
    }
}